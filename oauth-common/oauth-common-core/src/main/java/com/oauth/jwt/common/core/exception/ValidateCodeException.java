package com.oauth.jwt.common.core.exception;

import java.io.Serializable;

/**
 * 验证码错误类
 */
public class ValidateCodeException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    public ValidateCodeException() {
    }

    public ValidateCodeException(String message) {
        super(message);
    }
}
