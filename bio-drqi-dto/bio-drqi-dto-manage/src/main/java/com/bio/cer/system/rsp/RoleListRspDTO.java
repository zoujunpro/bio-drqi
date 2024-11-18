package com.bio.cer.system.rsp;

import com.bio.cer.base.PageDTO;
import lombok.Data;

import java.util.Date;

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
