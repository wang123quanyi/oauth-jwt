//package com.oauth.jwt.common.auth.component;
//
//import com.oauth.jwt.common.auth.annotation.Inner;
//import com.oauth.jwt.common.core.constants.SecurityConstant;
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.Ordered;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class InnerAspect implements Ordered {
//
//    private final HttpServletRequest request;
//    private final Logger log = LoggerFactory.getLogger(InnerAspect.class);
//
//    @Around("@annotation(inner)")
//    public Object around(ProceedingJoinPoint point, Inner inner) throws Throwable {
//        String header = request.getHeader(SecurityConstant.FROM);
//        if (inner.value() && !cn.hutool.core.util.StrUtil.equals(SecurityConstant.FROM_IN, header)) {
//            log.warn("访问接口 {} 没有权限", point.getSignature().getName());
//            throw new AccessDeniedException("Access is denied");
//        }
//        return point.proceed();
//    }
//
//    @Override
//    public int getOrder() {
//        return Ordered.HIGHEST_PRECEDENCE + 1;
//    }
//}
