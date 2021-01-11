package com.oauth.jwt.oauth.auth.service;

import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * oauth2认证服务，获取客户端详情
 */
public class MyClientDetailsService extends JdbcClientDetailsService {

    private static final Map<String, ClientDetails> CLIENTS = new HashMap<String, ClientDetails>();

    public static void clearCache() {
        CLIENTS.clear();
    }

    public static void clearCache(String clientId) {
        CLIENTS.remove(clientId);
    }

    public MyClientDetailsService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        //先判断缓存是否有  有的话 直接取缓存
        ClientDetails clientDetails = CLIENTS.get(clientId);
        if (clientDetails != null) {
            return clientDetails;
        }
        clientDetails = super.loadClientByClientId(clientId);
        CLIENTS.put(clientId, clientDetails);
        return clientDetails;
    }
}
