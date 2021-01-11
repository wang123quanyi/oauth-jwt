package com.oauth.jwt.common.data.annotation;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
@ComponentScan("com.oauth.jwt")
public @interface MyEnableFeignClients {

    String[] value() default {};

    String[] basePackages() default {"com.oauth.jwt"};

    Class<?>[] basePackageClasses() default {};

    Class<?>[] defaultConfiguration() default {};

    Class<?>[] clients() default {};
}
