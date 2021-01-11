package com.oauth.jwt.oauth.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.common.core.util.RedisUtil;
import com.oauth.jwt.common.data.util.LoginUserHolder;
import com.oauth.jwt.project.api.feign.RemoteSysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 认证服务
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenEndpoint tokenEndpoint;
    private final LoginUserHolder loginUserHolder;
    private final RemoteSysUserService remoteSysUserService;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    /**
     * Oauth2 登录认证,自定义登录成功返回值
     */
    @PostMapping(value = "/token")
    public R postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken token = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token", token.getValue());
        map.putAll(token.getAdditionalInformation());
//        log.info("\ntest:{}", loginUserHolder.getCurrentUser());
        return R.ok(map);
    }

//    /**
//     * 新增用户
//     */
//    @PostMapping(value = "/adduser")
//    public R add(@RequestBody Map<String, String> map) {
//        map.put("password", passwordEncoder.encode(map.get("password")).replace(SecurityConstant.BCRYPT, ""));
//        R r = remoteSysUserService.addUser(map);
//        return r;
//    }

    /**
     * 登出
     */
    @PostMapping(value = "/logout")
    public R logout() {
        JSONObject currentToken = loginUserHolder.getCurrentToken();
        String jti = currentToken.getString("jti");
        Date date = currentToken.getDate("exp-date");
        long minutes = (new Date().getTime() - date.getTime()) / 1000;
        redisUtil.set(jti, "测试退出", minutes);
        return R.ok();
    }

}
