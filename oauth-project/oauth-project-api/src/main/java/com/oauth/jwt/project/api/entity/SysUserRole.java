package com.oauth.jwt.project.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

/**
 * <p>
 * 用户角色表
 * </p>
 *
 * @author 徐一炜
 * @since 2019-10-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUserRole extends Model<SysUserRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 角色ID
     */
    private Integer roleId;


}
