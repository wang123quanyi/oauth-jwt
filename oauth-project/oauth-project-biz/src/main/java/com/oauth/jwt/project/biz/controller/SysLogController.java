package com.oauth.jwt.project.biz.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.project.api.entity.SysLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/log")
@RequiredArgsConstructor
public class SysLogController {

    @RequestMapping("/savelog")
    public R saveSysLog(@RequestBody SysLog sysLog) {
        log.info("\n sysLog: {}", JSON.toJSONString(sysLog, SerializerFeature.DisableCircularReferenceDetect));
        return R.ok();
    }
}
