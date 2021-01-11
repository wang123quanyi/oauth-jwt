package com.oauth.jwt.project.api.feign.factory;

import com.oauth.jwt.project.api.feign.RemoteSysLogService;
import com.oauth.jwt.project.api.feign.fallback.RemoteSysLogServiceFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteSysLogServiceFallbackFactory implements FallbackFactory<RemoteSysLogService> {
    @Override
    public RemoteSysLogService create(Throwable throwable) {
        RemoteSysLogServiceFallbackImpl remoteSysLogServiceFallback = new RemoteSysLogServiceFallbackImpl();
        remoteSysLogServiceFallback.setCause(throwable);
        return remoteSysLogServiceFallback;
    }
}
