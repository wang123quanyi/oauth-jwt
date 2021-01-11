package com.oauth.jwt.oauth.gateway.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;

/**
 * Hystrix 降级处理
 */
@Log4j2
@Component
public class HystrixFallbackHandler implements HandlerFunction<ServerResponse> {
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        //得到原始的请求的url
        Optional<Object> originalUris = serverRequest.attribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        //如果这个urls里面有东西
        originalUris.ifPresent(originalUri -> log.error("网关执行请求:{}失败，hystrix服务降级处理", originalUri));
        //返回空的response
        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue("服务异常,请稍后重试"));
    }
}
