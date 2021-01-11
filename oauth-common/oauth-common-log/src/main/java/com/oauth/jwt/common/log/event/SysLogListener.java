package com.oauth.jwt.common.log.event;

import com.oauth.jwt.project.api.entity.SysLog;
import com.oauth.jwt.project.api.feign.RemoteSysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 注解形式监听 异步监听日志事件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SysLogListener {

    private final RemoteSysLogService remoteSysLogService;

    @Async("taskExecutor")
    @Order
    @EventListener(SysLogEvent.class)
    public void saveSysLog(SysLogEvent event) {
        SysLog sysLog = (SysLog) event.getSource();
        // 保存日志
        remoteSysLogService.saveSysLog(sysLog);
    }

}
