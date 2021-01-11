package com.oauth.jwt.project.api.feign.factory;

import com.oauth.jwt.project.api.feign.RemoteSysUserService;
import com.oauth.jwt.project.api.feign.fallback.RemoteSysUserServiceFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteSysUserServiceFallbackFactory implements FallbackFactory<RemoteSysUserService> {

    public RemoteSysUserService create(Throwable throwable) {
        RemoteSysUserServiceFallbackImpl remoteSysUserServiceFallback = new RemoteSysUserServiceFallbackImpl();
        remoteSysUserServiceFallback.setCause(throwable);
        return remoteSysUserServiceFallback;
    }

}
