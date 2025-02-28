package com.bio.drqi.manage.user.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class UserListPageRspDTO  {
    /**
     * 主键ID
     */
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
    private Integer status;

    /**
     * 删除标志 N为删除 Y删除
     */
    private String delFlag;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
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
    private Integer postId;

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

}
