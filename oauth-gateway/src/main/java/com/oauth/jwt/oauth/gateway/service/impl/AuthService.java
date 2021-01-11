package com.oauth.jwt.oauth.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oauth.jwt.common.core.util.RedisUtil;
import com.oauth.jwt.oauth.gateway.service.IAuthService;
import com.oauth.jwt.oauth.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;


    public boolean hasPermission(String token, String url, String method) {
        //token是否有效
        if (invalidJwtAccessToken(token)) {
            return true;
        }
        return false;
    }

    public String getJwt(String token) {
        return jwtUtil.decode(token);
    }

    private boolean invalidJwtAccessToken(String token) {
        JSONObject decode = JSON.parseObject(jwtUtil.decode(token));
        //验证token时效、权限、是否加入缓存登出黑名单
        return true;
    }
}
