package com.oauth.jwt.project.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oauth.jwt.project.api.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    /**
     * 根据用户id获取用户菜单的标志
     *
     * @param userId
     * @return
     */
    List<String> findPermsByUserId(Integer userId);
}
