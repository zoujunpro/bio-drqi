package com.bio.drqi.system.rsp;

import lombok.Data;

@Data
public class RoleListRspDTO  {
    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

}
