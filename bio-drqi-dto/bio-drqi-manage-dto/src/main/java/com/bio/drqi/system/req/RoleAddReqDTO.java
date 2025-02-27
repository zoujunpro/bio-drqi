package com.bio.drqi.system.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RoleAddReqDTO {
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称参数缺失")
    private String roleName;

    /**备注*/
    private String remark;

    private List<Integer> menuList;
}
