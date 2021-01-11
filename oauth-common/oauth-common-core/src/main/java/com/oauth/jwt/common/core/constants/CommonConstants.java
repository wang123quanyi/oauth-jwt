package com.oauth.jwt.common.core.constants;

public interface CommonConstants {

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    Integer FAIL = 300;

    /**
     * 登录路径
     */
    String OAUTH_TOKEN_URL = "/oauth/token";

    /**
     * 注册路径
     */
    String USER_REGIST = "/regist";

    /**
     * 图形验证码
     */
    String XPTX_IMAGE_KEY = "XPTX_IMAGE_KEY";

    /**
     * 网关转发请求带的token解析出来的用户数据
     */
    String XPTX_CLIENT_TOKEN_USER = "x-client-token-user";
}
