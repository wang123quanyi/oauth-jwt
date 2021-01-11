package com.oauth.jwt.common.log.annotion;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SysOperaLog {
    String descrption() default "";//描述
}
