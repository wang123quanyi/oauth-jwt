package com.oauth.jwt.project.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oauth.jwt.project.api.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2019-10-12
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {


    @Select("SELECT r.role_name,ur.role_id \n" +
            "FROM (sys_role r LEFT JOIN sys_user_role ur ON r.role_id = ur.role_id ) \n" +
            "LEFT JOIN sys_user u ON u.user_id = ur.user_id WHERE u.user_id = #{userId}")
    List<SysUserRole> selectUserRoleListByUserId(Integer userId);
}
