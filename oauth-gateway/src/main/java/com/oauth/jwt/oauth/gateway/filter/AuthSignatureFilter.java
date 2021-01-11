package com.oauth.jwt.oauth.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.oauth.jwt.common.core.constants.CommonConstants;
import com.oauth.jwt.oauth.gateway.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Log4j2
//@Component
@RequiredArgsConstructor
public class AuthSignatureFilter implements GlobalFilter, Ordered {

    private static volatile Map<String, Date> ipMap= new ConcurrentHashMap<>();
    private final IAuthService authService;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @SneakyThrows
//    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 清洗请求头中用户信息参数
        ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove(CommonConstants.XPTX_CLIENT_TOKEN_USER)).build();
        //得到请求路径
        String urlPath = request.getPath().toString();
        boolean action = false;
        if (antPathMatcher.match("/auth/oauth/token", urlPath)) {
            action = true;
        }
        if (action) {
            return chain.filter(exchange.mutate().request(request).build());
        }
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String method = request.getMethodValue();
        String url = request.getPath().value();
        //如果请求未携带token信息，则直接跳出
        if (StringUtils.isEmpty(token)) {
            log.debug("url:{},method:{},headers:{},请求未携带token信息", url, method, request.getHeaders());
            return unauthorized(exchange);
        }
        //调用鉴权服务看看是否有权限
        boolean b = authService.hasPermission(token, url, method);
        //调用签权服务看用户是否有权限，若有权限进入下一个filter
        if (b) {
            //在请求头加入用户信息
            ServerHttpRequest.Builder builder = request.mutate();
            String claims = authService.getJwt(token);
            builder.header(CommonConstants.XPTX_CLIENT_TOKEN_USER, URLEncoder.encode(claims, "UTF-8"));
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        log.debug("url:{},method:{},headers:{},请求没有权限", url, method, request.getHeaders());
        return unauthorized(exchange);
    }

    /**
     * 认证未通过的请求走这里
     *
     * @param exchange
     * @return
     */
    public Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");

        InetSocketAddress inetSocketAddress = request.getRemoteAddress();
        //得到请求ip
        String ip = inetSocketAddress.getAddress().getHostAddress();
        log.info("非法请求，客户端ip:{}，URL:{}", ip, request.getPath().value());
        JSONObject message = new JSONObject();
        message.put("code", HttpStatus.UNAUTHORIZED);
        message.put("msg", "非法请求");
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        //nio的wrap操作
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -200;
    }

}
