package com.oauth.jwt.common.core.constants;


public interface SecurityConstant {

    /**
     * {bcrypt} 加密的特征码
     */
    String BCRYPT = "{bcrypt}";

    /**
     * noop是因为取出来的时候需要加密  然后加密方式是因为 PasswordEncoderFactories.createDelegatingPasswordEncoder()
     * oauth_client_details 表的字段 {scrypt}
     */
    String CLIENT_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    /**
     * JdbcClientDetailsService 查询语句
     */
    String BASE_FIND = "select " + CLIENT_FIELDS + " from oauth_client_details";

    /**
     * 默认的查询语句
     */
    String DEFAULT_FIND = BASE_FIND + " order by client_id";

    /**
     * 按条件client_id 查询
     */
    String DEFAULT_SELECT = BASE_FIND + " where client_id = ?";

    /**
     * jwt密钥
     */
    String SIGNING_KEY = "123";

    /**
     * Authorization认证开头是"bearer "
     */
    int BEARER_BEGIN_INDEX = 7;
}
