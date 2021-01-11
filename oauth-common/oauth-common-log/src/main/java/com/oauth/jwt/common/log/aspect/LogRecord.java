package com.oauth.jwt.common.log.aspect;

import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 测试时用
 */
@Log4j2
@Aspect
@Component
public class LogRecord {

    @Around("within(com.oauth.jwt..service.impl..*)")
    private Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime beginTime = LocalDateTime.now();
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        String className = signature.getDeclaringTypeName();
        String[] parameterNames = ((MethodSignature) signature).getParameterNames();
        Object[] argValues = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < parameterNames.length; i++) {
            sb.append(parameterNames[i]).append(":").append(argValues[i]).append(",");
        }
        String paramStr = sb.length() > 0 ? sb.substring(0, sb.length() - 1) + "]" : "";
        log.info("\n<INFO----\n类: {}\n方法: {}\n参数： {}", className, methodName, paramStr);
        Object result = joinPoint.proceed();
        Long opetime = Duration.between(beginTime, LocalDateTime.now()).toMillis();
        log.info("\n类: {}\n方法: {}\n参数： {}\n返回:{}\n响应时长： {}\n----INFO>\n", className, methodName, paramStr, result, opetime);
        return result;
    }

    @AfterReturning(pointcut = "within(com.oauth.jwt..controller..*)", returning = "rvt")
    private void afterReturning(JoinPoint joinPoint, Object rvt) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        String className = signature.getDeclaringTypeName();
        String[] parameterNames = ((MethodSignature) signature).getParameterNames();
        Object[] argValues = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < parameterNames.length; i++) {
            sb.append(parameterNames[i]).append(":").append(argValues[i]).append(",");
        }
        String paramStr = sb.length() > 0 ? sb.substring(0, sb.length() - 1) + "]" : "";
        log.info("\n<----\n类: {}\n方法: {}\n参数： {} \n__RETURN: {}\n---->\n\n", className, methodName, paramStr, JSONUtil.toJsonStr(rvt));

    }
}