package com.oauth.jwt.common.auth.handler;

import com.alibaba.fastjson.JSON;
import com.oauth.jwt.common.core.util.R;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 没有权限，授权失败时返回
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    //    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        R r = new R(403, "无权限", "path:" + request.getServletPath());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(r));
    }
}
