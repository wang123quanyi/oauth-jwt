package com.oauth.jwt.oauth.auth.controller;

import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.oauth.auth.exception.FeignOauthException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 登录失败自定义返回值
 */
@Log4j2
@RestControllerAdvice
public class Oauth2ExceptionHandler {

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public R handleOauth2(UsernameNotFoundException e) {
        return R.failed(405, e.getMessage());
    }

    @ExceptionHandler(value = FeignOauthException.class)
    public R handleOauth2(FeignOauthException e) {
        return R.failed(400, e.getMessage());
    }

    @ExceptionHandler(value = OAuth2Exception.class)
    public R handleOauth2(OAuth2Exception e) {
        return R.failed(405, e.getMessage());
    }
}
