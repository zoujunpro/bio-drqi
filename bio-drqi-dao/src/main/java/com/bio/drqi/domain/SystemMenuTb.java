package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date：2023-09-19
 * @Description：
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="system_menu_tb")
public class SystemMenuTb  implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父级菜单ID
     */
    private Integer parentId;

    /**
     * 系统标识
     */
    private Integer systemId;

    /**
     * 前端权限标识
     */
    private String frontPermissionCode;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 组件路径
     */
    private String componentPath;

    /**
     * 菜单类型 1菜单 2按钮
     */
    private String menuType;

    /**
     * 状态Y可用 N禁用
     */
    private String menuStatus;

    /**
     * 图标
     */
    private String menuIcon;

    /**
     * 排序方式，正序
     */
    private Integer orderNum;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 后端权限标识
     */
    private String backPermissionCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}