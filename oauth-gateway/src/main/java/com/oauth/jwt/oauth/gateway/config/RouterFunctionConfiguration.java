package com.oauth.jwt.oauth.gateway.config;

import com.oauth.jwt.oauth.gateway.handler.HystrixFallbackHandler;
import com.oauth.jwt.oauth.gateway.handler.ImageCodeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

/**
 * 路由配置信息 特殊请求直接在此处理，不进行路由转发
 */
@Component
@RequiredArgsConstructor
public class RouterFunctionConfiguration {

    private final HystrixFallbackHandler hystrixFallbackHandler;
    private final ImageCodeHandler imageCodeWebHandler;

    /**
     * 这里是code是验证码
     */
    @Bean
    public RouterFunction routerFunction() {
        return RouterFunctions.route(
                RequestPredicates.path("/factory")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), hystrixFallbackHandler)
                .andRoute(RequestPredicates.GET("/code")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), imageCodeWebHandler);
    }
}
