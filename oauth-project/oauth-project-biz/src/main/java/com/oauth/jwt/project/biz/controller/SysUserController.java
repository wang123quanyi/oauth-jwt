package com.oauth.jwt.project.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.common.data.annotation.Idempotent;
import com.oauth.jwt.common.data.util.LoginUserHolder;
import com.oauth.jwt.common.log.annotion.SysOperaLog;
import com.oauth.jwt.project.api.entity.SysUser;
import com.oauth.jwt.project.api.entity.UserDetailsInfo;
import com.oauth.jwt.project.biz.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

//import com.oauth.jwt.common.data.util.LoginUserHolder;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author syl
 * @since 2020-11-04
 */
@RestController
@Log4j2
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final LoginUserHolder loginUserHolder;

    //    @RequestMapping("/add")
//    public int add(@RequestBody SysUser sysUser) {
//        return sysUserService.add(sysUser);
//    }
//
    @Idempotent(2)
    @SysOperaLog(descrption = "测试")
    @RequestMapping("/update")
    public R update() {
        JSONObject currentUser = loginUserHolder.getCurrentUser();
        log.info("\n当前用户：{}", currentUser);
        return R.ok();
//        return sysUserService.updateData(sysUser);
    }

    @RequestMapping("/lubun")
    public R<UserDetailsInfo> loadUserByUsername(@RequestParam("username") String userName) {

//        log.info("\n 内部调用：{}", r);
        SysUser sysUser1 = new SysUser();
        sysUser1.setUsername(userName);
        sysUser1.setPassword("$2a$10$FMhcGm3XmFp9lMItQlyMhOUIFvyc1hunHF7U0Hxx221CYR2c8mwp6");
        String role = "ROLE_ADMIN";
        UserDetailsInfo userDetailsInfo1 = new UserDetailsInfo();
        userDetailsInfo1.setSysUser(sysUser1);
        Set<String> strings = new HashSet<>();
        strings.add(role);
        userDetailsInfo1.setPermissions(strings);
        return R.ok(userDetailsInfo1);
//        SysUser user = sysUserService.findUserByUserIdOrUserNameOrPhone(userName);
//        if (user == null) {
//            return R.failed(String.format("%s用户为空", userName));
//        }
//        return R.ok(sysUserService.findUserInfo(user));
//        return R.ok(user);
    }
}
