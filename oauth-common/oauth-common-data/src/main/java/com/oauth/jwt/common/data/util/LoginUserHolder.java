package com.oauth.jwt.common.data.util;

import com.alibaba.fastjson.JSONObject;
import com.oauth.jwt.common.core.constants.CommonConstants;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

/**
 * 获取登录用户信息,服务间互调时获取不了，需要传递
 */
@Component
public class LoginUserHolder {

    @Autowired
    protected HttpServletRequest request;

    /**
     * 获取当前操作账号信息
     */
    @SneakyThrows
    public JSONObject getCurrentUser() {
        String userStr = URLDecoder.decode(request.getHeader(CommonConstants.XPTX_CLIENT_TOKEN_USER), "UTF-8");
        JSONObject userJsonObject = JSONObject.parseObject(userStr);
        return userJsonObject;
    }

    /**
     * 获取当前操作token信息，在登出时用
     */
    @SneakyThrows
    public JSONObject getCurrentToken() {
        String userStr = URLDecoder.decode(request.getHeader(CommonConstants.XPTX_CLIENT_TOKEN_USER), "UTF-8");
        JSONObject userJsonObject = JSONObject.parseObject(userStr);
        return userJsonObject;
    }

}
