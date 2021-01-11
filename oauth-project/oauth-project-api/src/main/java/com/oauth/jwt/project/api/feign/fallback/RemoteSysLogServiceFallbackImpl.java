package com.oauth.jwt.project.api.feign.fallback;

import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.project.api.entity.SysLog;
import com.oauth.jwt.project.api.feign.RemoteSysLogService;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RemoteSysLogServiceFallbackImpl implements RemoteSysLogService {
    @Setter
    private Throwable cause;

    @Override
    public R saveSysLog(SysLog sysLog) {
        log.error("feign 同步日志失败cause:{}, sysLog:{}", cause, sysLog);
        return R.failed();
    }
}
