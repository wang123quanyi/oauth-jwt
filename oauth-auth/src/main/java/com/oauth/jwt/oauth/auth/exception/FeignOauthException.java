package com.oauth.jwt.oauth.auth.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class FeignOauthException extends OAuth2Exception {
    public FeignOauthException(String msg) {
        super(msg);
    }
}
