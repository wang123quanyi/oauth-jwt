package com.oauth.jwt.project.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oauth.jwt.project.api.entity.SysMenu;
import com.oauth.jwt.project.biz.mapper.SysMenuMapper;
import com.oauth.jwt.project.biz.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-10-12
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    public List<String> findPermsByUserId(Integer userId) {
        return baseMapper.findPermsByUserId(userId);
    }
}
