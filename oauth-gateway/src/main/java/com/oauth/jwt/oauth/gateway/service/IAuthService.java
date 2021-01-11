package com.oauth.jwt.oauth.gateway.service;

public interface IAuthService {
    /**
     * 调用签权服务，判断用户是否有权限
     */
    boolean hasPermission(String token, String url, String method);

    /**
     * 从认证信息中提取jwt token 对象
     *
     * @param token
     * @return
     */
    String getJwt(String token);
}
