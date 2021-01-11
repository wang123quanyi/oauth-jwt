package com.oauth.jwt.oauth.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.jwt.common.core.constants.CommonConstants;
import com.oauth.jwt.common.core.exception.ValidateCodeException;
import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.common.core.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/**
 * 图片验证码过滤器
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class ImageCodeFilter extends AbstractGatewayFilterFactory {

    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestUrl = request.getURI().getPath();
            //如果不包含登录 则直接放行
            if (!requestUrl.contains(CommonConstants.OAUTH_TOKEN_URL)) {
                return chain.filter(exchange);
            }
            try {
                //登录的情况检查code
//                checkCode(request);
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.PRECONDITION_REQUIRED);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                final String errMsg = e.getMessage();
                return response.writeWith(Mono.create(monoSink -> {
                    try {
                        byte[] bytes = objectMapper.writeValueAsBytes(R.failed(errMsg));
                        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);

                        monoSink.success(dataBuffer);
                    } catch (JsonProcessingException jsonProcessingException) {
                        log.error("对象输出异常", jsonProcessingException);
                        monoSink.error(jsonProcessingException);
                    }
                }));
            }
            return chain.filter(exchange);
        };
    }

    /**
     * 验证验证码 如果错误 网关报500
     *
     * @param request
     * @SneakyThrows可以隐性的抛出异常 不然后面还要加throws什么
     */
    @SneakyThrows
    private void checkCode(ServerHttpRequest request) {
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        // 验证码
        String code = queryParams.getFirst("code");
        // 随机标识
        String t = queryParams.getFirst("t");
        // 验证验证码流程
        if (StrUtil.isBlank(code)) {
            throw new ValidateCodeException("验证码不能为空");
        }
        // 从redis中获取之前保存的验证码跟前台传来的验证码进行匹配
        Object kaptcha = redisUtil.get(CommonConstants.XPTX_IMAGE_KEY + t);
        if (kaptcha == null) {
            throw new ValidateCodeException("验证码已失效");
        }
        if (!code.toLowerCase().equals(kaptcha)) {
            throw new ValidateCodeException("验证码错误");
        }
    }
}
