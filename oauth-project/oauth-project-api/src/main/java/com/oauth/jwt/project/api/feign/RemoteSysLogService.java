package com.oauth.jwt.project.api.feign;

import com.oauth.jwt.common.core.constants.ServiceNameConstants;
import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.project.api.entity.SysLog;
import com.oauth.jwt.project.api.feign.factory.RemoteSysLogServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteSysLogService", value = ServiceNameConstants.PROJECT, fallbackFactory = RemoteSysLogServiceFallbackFactory.class)
public interface RemoteSysLogService {

    /**
     * 同步日志
     */
    @PostMapping("/log/savelog")
    R saveSysLog(@RequestBody SysLog sysLog);
}
