package com.oauth.jwt.project.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;


/**
 * <p>
 * 系统角色表
 * </p>
 *
 * @author 徐一炜
 * @since 2019-10-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysRole extends Model<SysRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色主键
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Integer roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识（0-正常,1-删除）
     */
    private String delFlag;

    /**
     * 数据权限类型
     */
    private Integer dsType;

    /**
     * 数据权限范围 1 全部 2 本级 3 本级以及子级 4 自定义
     */
    private String dsScope;

    /**
     * 租户id
     */
    private Integer tenantId;


}
