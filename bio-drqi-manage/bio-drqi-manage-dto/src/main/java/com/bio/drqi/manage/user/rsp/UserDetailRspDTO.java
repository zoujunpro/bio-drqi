package com.bio.drqi.manage.user.rsp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDetailRspDTO {
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
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private String sex;
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
     * 工号
     */
    private String jobNum;

    /**是否是超级管理员*/
    private boolean admin;

    private String password;


    private List<System> systemList=new ArrayList<>();


    private List<Role> roleList=new ArrayList<>();


    private List<Permissions> permissionsList=new ArrayList<>();


    @Data
    public static class System {
        private String systemName;
        private Integer systemId;
    }

    @Data
    public static class Role {
        private Integer roleId;
        private String roleName;
    }

    @Data
    public static class Permissions {
        /**
         * 前端权限编码
         */
        private String frontPermissionCode;
        /**
         * 后端权限编码
         */
        private String backPermissionCode;

        /*父ID*/
        private Integer parentId;

        /*ID*/
        private Integer id;

        /**
         * 排序方式，正序
         */
        private Integer orderNum;
        /**
         * 菜单图标
         */
        private String menuIcon;
        /**
         * 组件路径
         */
        private String componentPath;
        /**
         * 菜单类型 1菜单，2按钮
         */
        private String menuType;
        /**
         * 系统编码
         */
        private String systemCode;

        /**菜单名称*/
        private String menuName;

    }
}
