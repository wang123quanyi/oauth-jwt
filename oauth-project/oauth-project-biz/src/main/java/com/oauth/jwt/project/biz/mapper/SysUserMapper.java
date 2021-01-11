package com.oauth.jwt.project.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oauth.jwt.project.api.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author syl
 * @since 2020-11-04
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser getByUserIdOrUserNameOrPhone(String userName);
}
