package com.oauth.jwt.common.log.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSONObject;
import com.oauth.jwt.common.core.constants.CommonConstants;
import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.common.log.event.SysLogEvent;
import com.oauth.jwt.common.log.util.LogUtil;
import com.oauth.jwt.project.api.entity.SysLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * 切面注解得到请求数据 -> ②发布监听事件 -> ③异步监听日志入库
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {
    private ThreadLocal<SysLog> sysLogThreadLocal = new ThreadLocal<>();

    /**
     * 事件发布是由ApplicationContext对象管控的，发布时间前需要注入ApplicationContext对象
     * 调用publishEvent方法完成时间发布
     */
    @Autowired
    private ApplicationContext applicationContext;

    @Pointcut("@annotation(com.oauth.jwt.common.log.annotion.SysOperaLog)")
    public void sysLogAspect() {
    }

    /**
     * 拦截控制层操作日志
     */
    @Before(value = "sysLogAspect()")
    public void recordLog(JoinPoint joinPoint) throws Throwable {
        SysLog sysLog = new SysLog();
        //将当前实体保存到threadLocal
        sysLogThreadLocal.set(sysLog);
        //开始时间
        long beginTime = Instant.now().toEpochMilli();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String userStr = URLDecoder.decode(request.getHeader(CommonConstants.XPTX_CLIENT_TOKEN_USER), "UTF-8");
        JSONObject userJsonObject = JSONObject.parseObject(userStr);
        sysLog.setUserId(userJsonObject.getString("user_name"));
        sysLog.setActionUrl(URLUtil.getPath(request.getRequestURI()));
        sysLog.setStartTime(LocalDateTime.now());
        String ip = ServletUtil.getClientIP(request);
        sysLog.setIp(ip);
        //访问目标方法的参数 可动态改变参数值
        Object[] args = joinPoint.getArgs();
        //获取执行的方法名
        sysLog.setActionMethod(joinPoint.getSignature().getName());
        // 类名
        sysLog.setClassPath(joinPoint.getTarget().getClass().getName());
        sysLog.setActionMethod(joinPoint.getSignature().getName());
        sysLog.setFinishTime(LocalDateTime.now());
        // 参数
        sysLog.setParams(Arrays.toString(args));
        sysLog.setDescription(LogUtil.getControllerMethodDescription(joinPoint));
        long endTime = Instant.now().toEpochMilli();
        sysLog.setConsumingTime(endTime - beginTime);
    }

    /**
     * 返回通知
     */
    @AfterReturning(returning = "ret", pointcut = "sysLogAspect()")
    public void doAfterReturning(Object ret) {
        //得到当前线程的log对象
        SysLog sysLog = sysLogThreadLocal.get();
        // 处理完请求，返回内容
        R r = Convert.convert(R.class, ret);
        if (r.getCode() == 200) {
            // 正常返回
            sysLog.setType(1);
        } else {
            sysLog.setType(2);
            sysLog.setExDetail(r.getMsg());
        }
        // 发布事件
        applicationContext.publishEvent(new SysLogEvent(sysLog));
        //移除当前log实体
        sysLogThreadLocal.remove();
    }

    /**
     * 异常通知
     */
    @AfterThrowing(pointcut = "sysLogAspect()", throwing = "e")
    public void doAfterThrowable(Throwable e) {
        SysLog sysLog = sysLogThreadLocal.get();
        // 异常
        sysLog.setType(2);
        // 异常对象
        sysLog.setExDetail(LogUtil.getStackTrace(e));
        // 异常信息
        sysLog.setExDesc(e.getMessage());
        // 发布事件
        applicationContext.publishEvent(new SysLogEvent(sysLog));
        //移除当前log实体
        sysLogThreadLocal.remove();
    }
}
