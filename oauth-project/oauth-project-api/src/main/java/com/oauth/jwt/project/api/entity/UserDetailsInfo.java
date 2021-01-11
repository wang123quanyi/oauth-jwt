package com.oauth.jwt.project.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * 登录之后保存用户信息
 */
@Setter
@Getter
@ToString
@Accessors(chain = true)
public class UserDetailsInfo implements Serializable {

    /**
     * 用户的基本信息
     */
    private SysUser sysUser;

    /**
     * 用户权限
     */
    private Set<String> permissions;
}
