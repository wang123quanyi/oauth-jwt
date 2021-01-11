package com.oauth.jwt.oauth.auth.service;

import com.oauth.jwt.common.auth.entity.SecurityUser;
import com.oauth.jwt.common.core.constants.SecurityConstant;
import com.oauth.jwt.common.core.util.R;
import com.oauth.jwt.oauth.auth.exception.FeignOauthException;
import com.oauth.jwt.project.api.entity.SysUser;
import com.oauth.jwt.project.api.entity.UserDetailsInfo;
import com.oauth.jwt.project.api.feign.RemoteSysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final RemoteSysUserService remoteSysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        R<UserDetailsInfo> info = remoteSysUserService.loadUserByUsername(username);
        if (200 != info.getCode()) {
            throw new FeignOauthException("请稍后重试");
        }
        if (info.getData() == null || info.getData().getSysUser() == null) {
            log.debug("登录用户:" + username + "不存在");
            throw new UsernameNotFoundException("登录用户:" + username + "不存在");
        }
        //得到用户信息
        UserDetailsInfo userDetailsInfo = info.getData();
        SysUser sysUser = userDetailsInfo.getSysUser();
        //得到该用户所拥有的的角色id集合 ROLE_role_id
        Set<String> permissions = userDetailsInfo.getPermissions();
        //把角色id转换成list
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(permissions.toArray(new String[0]));
        //{bcrypt}数据库明文保存  取出来比较的时候密文
        return new SecurityUser(sysUser.getUserId(), username, SecurityConstant.BCRYPT + sysUser.getPassword(), authorities);
    }
}
