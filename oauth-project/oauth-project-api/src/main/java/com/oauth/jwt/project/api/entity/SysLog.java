package com.oauth.jwt.project.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysLog extends Model<SysLog> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    /**
     * 操作IP
     */
    private String ip;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 操作类型 1 操作记录 2异常记录
     */
    private Integer type;

    /**
     * 操作人ID
     */
    private String userId;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求方法
     */
    private String actionMethod;

    /**
     * 请求url
     */
    private String actionUrl;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 类路径
     */
    private String classPath;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 消耗时间
     */
    private Long consumingTime;

    /**
     * 异常详情信息 堆栈信息
     */
    private String exDetail;

    /**
     * 异常描述 e.getMessage
     */
    private String exDesc;
}
