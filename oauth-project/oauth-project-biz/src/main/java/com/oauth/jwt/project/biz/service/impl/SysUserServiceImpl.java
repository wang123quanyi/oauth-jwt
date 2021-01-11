package com.oauth.jwt.project.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oauth.jwt.project.api.entity.SysUser;
import com.oauth.jwt.project.api.entity.UserDetailsInfo;
import com.oauth.jwt.project.biz.mapper.SysUserMapper;
import com.oauth.jwt.project.biz.service.SysMenuService;
import com.oauth.jwt.project.biz.service.SysUserRoleService;
import com.oauth.jwt.project.biz.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author syl
 * @since 2020-11-04
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysMenuService sysMenuService;
    private final SysUserRoleService userRoleService;

    public int add(SysUser sysUser) {
        return baseMapper.insert(sysUser);
    }


    public int updateData(SysUser sysUser) {
        return baseMapper.updateById(sysUser);
    }

    public SysUser findUserByUserIdOrUserNameOrPhone(String userName) {
        SysUser sysUser1 = new SysUser();
        sysUser1.setUsername(userName);
        System.out.println("findUserByUserIdOrUserNameOrPhone");
        return sysUser1;
//        return sysUserMapper.getByUserIdOrUserNameOrPhone(userName);
    }

    public UserDetailsInfo findUserInfo(SysUser user) {
        UserDetailsInfo userDetailsInfo = new UserDetailsInfo();
        //获取菜单的perms
        Set<String> perms = findPermsByUserId(user.getUserId());
        //获取角色的id
        Set<String> roleIds = findRoleIdByUserId(user.getUserId());
        //返回信息
        perms.addAll(roleIds);
        userDetailsInfo.setSysUser(user);
        userDetailsInfo.setPermissions(perms);
        return userDetailsInfo;
    }

    public Set<String> findPermsByUserId(Integer userId) {
        Set<String> perms = sysMenuService.findPermsByUserId(userId).stream().filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        return perms;
    }

    public Set<String> findRoleIdByUserId(Integer userId) {
        return userRoleService
                .selectUserRoleListByUserId(userId)
                .stream()
                .map(sysUserRole -> "ROLE_" + sysUserRole.getRoleId())
                .collect(Collectors.toSet());
    }

}
