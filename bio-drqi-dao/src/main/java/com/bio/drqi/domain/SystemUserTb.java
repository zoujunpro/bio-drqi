package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @TableName system_user_tb
 */
@TableName(value ="system_user_tb")
@Data
public class SystemUserTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名（登录用）
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态 0可用 1禁用
     */
    private String status;

    /**
     * 删除标志 N未删除 Y已删除
     */
    private String delFlag;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别 1男 2女
     */
    private String sex;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String telephone;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 被谁创建
     */
    private String createBy;

    /**
     * 职位ID
     */
    private Integer jobId;

    /**
     * 说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 工号
     */
    private String jobNum;

    /**
     * 登录失败次数
     */
    private Integer loginFailNum;

    /**
     * 头像地址
     */
    private String portraitUrl;

    /**
     * 上级领导ID
     */
    private Integer superiorId;

    /**
     * 负责人标识，Y,N
     */
    private String managerFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}