package com.oauth.jwt.oauth.auth.exception;

import java.io.Serializable;

public class JwtException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public JwtException() {
    }

    public JwtException(String message) {
        super(message);
    }
}
