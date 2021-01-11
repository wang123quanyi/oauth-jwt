package com.oauth.jwt.project.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oauth.jwt.project.api.entity.SysUserRole;
import com.oauth.jwt.project.biz.mapper.SysUserRoleMapper;
import com.oauth.jwt.project.biz.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-10-12
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {


    public List<SysUserRole> selectUserRoleListByUserId(Integer userId) {
        return baseMapper.selectUserRoleListByUserId(userId);
    }
}
