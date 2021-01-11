package com.oauth.jwt.common.auth.config;

import com.oauth.jwt.common.auth.handler.AuthExceptionEntryPoint;
import com.oauth.jwt.common.auth.handler.CustomAccessDeniedHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * 资源服务器，在需要放行的资源重写这个服务
 */
@Log4j2
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * security的鉴权排除的url列表
     */
    private static final String[] EXCLUDED_AUTH_PAGES = {"/oauth/**"};

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(EXCLUDED_AUTH_PAGES).permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.authenticationEntryPoint(new AuthExceptionEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler());
    }

}
