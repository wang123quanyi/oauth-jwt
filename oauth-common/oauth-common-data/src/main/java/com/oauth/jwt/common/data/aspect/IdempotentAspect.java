package com.oauth.jwt.common.data.aspect;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.oauth.jwt.common.data.annotation.Idempotent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Order(-1)
@Component
public class IdempotentAspect {

    @Resource
    private Redisson redisson;
    private String RMAPCACHE_KEY = "idempotent";
    private static ThreadLocal<String> idempotent = new ThreadLocal<>();


    @Pointcut("@annotation(com.oauth.jwt.common.data.annotation.Idempotent)")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(idempotent)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        Object result;
        if (!isDoing()) {
            try {
                doing();
                result = getResult(joinPoint, idempotent.value() * 1000);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            } finally {
                done();
            }
        } else {
            try {
                result = joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private Object getResult(ProceedingJoinPoint joinPoint, int value) throws Throwable {
        String argsJsonStr = JSONUtil.toJsonStr(joinPoint.getArgs());
        log.info("\nargsJsonStr:{}", argsJsonStr);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();
        log.info("\nrequestURI:{}", requestURI);
        String ip = getIp(request);
        log.info("\nip:{}", ip);
        String lockName = new StringBuffer(requestURI).append(":").append(ip).append(argsJsonStr).toString();
        log.info("\nlockName:{}", lockName);
        String key = MD5.create().digestHex(lockName.getBytes());
        RMapCache<Object, Object> mapCache = redisson.getMapCache(RMAPCACHE_KEY);
        Object result = mapCache.get(key);
        if (result == null) {
            RLock lock = redisson.getLock(key);
            try {
                lock.lock(value, TimeUnit.MILLISECONDS);
                result = mapCache.get(key);
                if (result != null) {
                    return result;
                }
                result = joinPoint.proceed();
                if (result != null) {
                    mapCache.putIfAbsent(key, result, value - 50, TimeUnit.MILLISECONDS);
                }
            } catch (Throwable e) {
                throw e;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    private void doing() {
        idempotent.set("1");
    }

    private boolean isDoing() {
        if ("1".equals(idempotent.get())) {
            return true;
        }
        return false;
    }

    private void done() {
        idempotent.remove();
    }

    private String getIp(HttpServletRequest request) {
        String ip = null;
        String ipAddresses = request.getHeader("X-Forwarded-For");
        String unknown = "unknown";
        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("X-Real-IP");
        }
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        log.info("\n^-^ Current clientIP:{} ^-^", ip);
        return ip;
    }
}
