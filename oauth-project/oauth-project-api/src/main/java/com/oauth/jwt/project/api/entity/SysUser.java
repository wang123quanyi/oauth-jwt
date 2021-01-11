package com.oauth.jwt.project.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;

import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author syl
 * @since 2020-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUser extends Model<SysUser> {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String username;

    private String password;

    private Integer deptId;

    private Integer jobId;

    private String email;

    private String phone;

    private String avatar;

    private Date createTime;

    private Date updateTime;

    private String lockFlag;

    private String delFlag;

}
