package com.bio.drqi.manage.system.req;

import com.bio.drqi.manage.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RoleEditReqDTO {

    private Integer roleId;
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称缺失")
    private String roleName;

    /**备注*/
    private String remark;

    private List<Integer> menuList;

    @EnumValue(strValues = {"Y","N"},message = "只能填写 N,Y")
    private String status;
}
