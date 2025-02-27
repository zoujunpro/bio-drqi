package com.bio.drqi.system.req;

import com.bio.drqi.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MenuAddReqDTO {

    /**
     * 父级菜单ID
     */
    private Integer parentId;

    /**
     * 系统标识
     */
    @NotNull(message = "系统标识参数缺失")
    private Integer systemId;
    /**
     * 前端权限标识
     */
    private String frontPermissionCode;
    /**
     * 菜单名称
     */
    @NotNull(message = "菜单名称缺失")
    private String menuName;
    /**
     * 组件路径
     */
    private String componentPath;
    /**
     * 菜单类型 1菜单 2按钮
     */
    @EnumValue(strValues = {"1", "2"}, message = "菜单类型只能是1和2")
    private String menuType;
    /**
     * 图标
     */
    private String menuIcon;
    /**
     * 排序方式，正序
     */
    private Integer orderNum;
    /**
     * 后端权限标识
     */
    private String backPermissionCode;


}
