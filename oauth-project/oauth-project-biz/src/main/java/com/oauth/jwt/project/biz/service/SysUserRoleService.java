package com.oauth.jwt.project.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oauth.jwt.project.api.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleService extends IService<SysUserRole> {
    /**
     * 根据用户id获取该用户的角色id集合
     *
     * @param userId
     * @return
     */
    List<SysUserRole> selectUserRoleListByUserId(Integer userId);

}
