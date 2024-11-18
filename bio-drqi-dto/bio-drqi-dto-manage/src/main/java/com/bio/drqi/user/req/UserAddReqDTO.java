package com.bio.drqi.user.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserAddReqDTO {

    /**
     * 用户名（登录用）
     */
    @NotBlank(message = "用户名必填")
    private String username;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称必填")
    private String nickname;



    /**
     * 邮箱
     */
    @NotBlank(message = "email必填")
    private String email;

    /**
     * 性别
     */
    @NotBlank(message = "性别必填")
    private String sex;

    /**
     * 密码
     */
    @NotBlank(message = "密码必填")
    private String password;

    /**
     * 电话
     */
    @NotBlank(message = "手机号必填")
    private String telephone;

    /**
     * 部门ID
     */
    @NotNull(message = "部门必填")
    private Integer deptId;


    /**
     * 职位ID
     */
    @NotNull(message = "职位必填")
    private Integer postId;

    /**
     * 说明
     */
    private String remark;

    /**
     * 工号
     */
    @NotBlank(message = "工号必填")
    private String jobNum;

    private List<Integer> roleIdList;


    private List<Integer> systemIdList;
}
