package com.oauth.jwt.oauth.gateway.exception;

import java.io.Serializable;

/**
 * jwt异常信息
 */
public class JwtException extends RuntimeException implements Serializable {

    public JwtException() {
    }

    public JwtException(String message) {
        super(message);
    }
}
