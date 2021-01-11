package com.oauth.jwt.oauth.auth.config;

import com.alibaba.fastjson.JSON;
import com.oauth.jwt.common.auth.entity.SecurityUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt增强，自定i扩展信息
 */
public class JWTokenEnhancer implements TokenEnhancer {

    //    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("jwt-ext", "JWT 扩展信息");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 30); //30分钟内token有效
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        info.put("exp-date", formatter.format(nowTime.getTime()));
        SecurityUser user = (SecurityUser) authentication.getUserAuthentication().getPrincipal();
        Map map = JSON.parseObject(JSON.toJSONString(user), Map.class);
        map.remove("password");
        info.putAll(map);
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
        return oAuth2AccessToken;
    }

}
