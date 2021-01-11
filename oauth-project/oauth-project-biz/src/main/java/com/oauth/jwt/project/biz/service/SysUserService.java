package com.oauth.jwt.project.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oauth.jwt.project.api.entity.SysUser;
import com.oauth.jwt.project.api.entity.UserDetailsInfo;

import java.util.Set;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author syl
 * @since 2020-11-04
 */
public interface SysUserService extends IService<SysUser> {


    /**
     * 添加
     *
     * @param sysUser
     * @return int
     */
    int add(SysUser sysUser);


    /**
     * 修改
     *
     * @param sysUser
     * @return int
     */
    int updateData(SysUser sysUser);

    /**
     * 根据用户id或者用户名称或者手机号码来查询用户
     *
     * @param userName
     * @return
     */
    SysUser findUserByUserIdOrUserNameOrPhone(String userName);

    /**
     * 根据用户来获取用户授权信息 给授权服务器使用
     *
     * @param user
     * @return
     */
    UserDetailsInfo findUserInfo(SysUser user);

    /**
     * 根据用户id获取该用户所拥有的菜单标志
     *
     * @param userId
     * @return
     */
    Set<String> findPermsByUserId(Integer userId);

    /**
     * 根据用户id获取该用户的所拥有的角色id
     *
     * @param userId
     * @return
     */
    Set<String> findRoleIdByUserId(Integer userId);
}
