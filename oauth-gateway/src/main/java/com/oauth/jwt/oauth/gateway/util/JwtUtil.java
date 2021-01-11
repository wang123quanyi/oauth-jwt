package com.oauth.jwt.oauth.gateway.util;

import com.oauth.jwt.common.core.constants.SecurityConstant;
import com.oauth.jwt.oauth.gateway.exception.JwtException;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * jwt工具类
 */
@Component
public class JwtUtil {

    /**
     * Authorization认证开头是"bearer "
     */
    private static final int BEARER_GEGIN_INDEX = 7;

    /**
     * jwt验签
     */
    private static MacSigner verifier;

    /**
     * 解密token
     */
    public String decode(String token) {
        verifier = Optional.ofNullable(verifier).orElse(new MacSigner(SecurityConstant.SIGNING_KEY));
        try {
            token = StringUtils.substring(token, BEARER_GEGIN_INDEX);
            String claims = JwtHelper.decodeAndVerify(token, verifier).getClaims();
            return claims;
        } catch (RuntimeException e) {
            throw new JwtException("jwtError,校验失败");
        }
    }

}
