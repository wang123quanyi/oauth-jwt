package com.oauth.jwt.common.data.annotation;

import java.lang.annotation.*;

/**
 * 处理同一账号高频调用同一接口
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Idempotent {
    /**
     * 执行时长：单位秒
     */
    int value() default 1;
}
