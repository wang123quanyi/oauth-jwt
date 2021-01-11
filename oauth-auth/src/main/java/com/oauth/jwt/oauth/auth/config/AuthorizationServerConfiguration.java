package com.oauth.jwt.oauth.auth.config;

import com.oauth.jwt.common.core.constants.SecurityConstant;
import com.oauth.jwt.oauth.auth.service.MyClientDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证配置
 */
@Log4j2
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    private final DataSource dataSource;

    private final TokenStore tokenStore;

    private final JwtAccessTokenConverter tokenConverter;

    /**
     * 端点配置
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancerList = new ArrayList<TokenEnhancer>();
        enhancerList.add(jwtTokenEnhancer());
        enhancerList.add(tokenConverter);
        enhancerChain.setTokenEnhancers(enhancerList);
        endpoints
                //指定token存储位置
                .tokenStore(tokenStore)
                // 配置JwtAccessToken转换器
                .accessTokenConverter(tokenConverter)
                .tokenEnhancer(enhancerChain)
                //指定认证管理器,当你选择了资源所有者密码（password）授权类型的时候，需设置这个属性注入一个 AuthenticationManager 对象。
                .authenticationManager(authenticationManager);
    }

    /**
     * 客户端配置
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer client) throws Exception {
        MyClientDetailsService clientDetailsService = new MyClientDetailsService(dataSource);
        clientDetailsService.setSelectClientDetailsSql(SecurityConstant.DEFAULT_SELECT);
        clientDetailsService.setFindClientDetailsSql(SecurityConstant.DEFAULT_FIND);
        client.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
    }

    @Bean
    public TokenEnhancer jwtTokenEnhancer() {
        return new JWTokenEnhancer();
    }
}
