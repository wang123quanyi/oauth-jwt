package com.oauth.jwt.common.data.aspect;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.oauth.jwt.common.data.annotation.Idempotent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Order(-1)
@Component
public class IdempotentAspect {

    @Resource
    private Redisson redisson;
    private MD5 md5 = MD5.create();
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
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String lockName = new StringBuffer(request.getRequestURI()).append(":").append(getIp(request)).toString();
                result = getResult(joinPoint,lockName,idempotent);
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

    private Object getResult(ProceedingJoinPoint joinPoint, String lockName, Idempotent idempotent) throws Throwable {
        RMapCache<Object, Object> mapCache = redisson.getMapCache(RMAPCACHE_KEY);
        String key = md5.digestHex(new StringBuffer(lockName).append(JSONUtil.toJsonStr(joinPoint.getArgs())).toString().getBytes());
        Object result = mapCache.get(key);
        if (result == null) {
            RLock lock = redisson.getLock(lockName);
            try {
                int value =  idempotent.value() * 1000;
                lock.lock(value, TimeUnit.MILLISECONDS);
                result = mapCache.get(key);
                if (result != null) {
                    return result;
                }
                result = joinPoint.proceed(joinPoint.getArgs());
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
