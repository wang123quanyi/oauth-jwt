package com.oauth.jwt.common.auth.handler;

import com.oauth.jwt.common.core.util.R;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义认证异常信息
 */
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    /**
     * token错误时进入
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
//    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        R r = new R(401, "非法访问资源,访问此资源需要完全身份验证", "path:" + request.getServletPath());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), r);
        } catch (Exception e) {
            throw new ServletException();
        }
    }
}
