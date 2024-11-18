package com.bio.drqi.system.rsp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuListPageRspDTO {

    private List<Menu> menuList = new ArrayList<Menu>();

    @Data
    public static class Menu {
        /**
         * 主键ID
         */
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
         * 后端权限标识
         */
        private String backPermissionCode;

        private List<Menu> children=new ArrayList<>();

    }

    public MenuListPageRspDTO toTree() {
        List<Menu> treeList = new ArrayList<Menu>();
        for (Menu menu : menuList) {
            if (menu.parentId == null || menu.parentId == 0) {
                treeList.add(menu);
            }
            for (Menu child : menuList) {
                if (menu.id.equals(child.parentId)) {
                    menu.children.add(child);
                }
            }
        }
        this.menuList = treeList;
        return this;
    }
}
