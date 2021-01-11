package com.oauth.jwt.common.log.event;

import com.oauth.jwt.project.api.entity.SysLog;
import org.springframework.context.ApplicationEvent;

/**
 * 系统日志事件
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(SysLog sysLog) {
        super(sysLog);
    }
}
