package com.oauth.jwt.common.auth.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {
    /**
     * 是否AOP统一处理
     */
    boolean value() default true;
}

