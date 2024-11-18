package com.bio.drqi.project.rsp;

import lombok.Data;

@Data
public class ProjectUserAllRspDTO {

    /**
     * 项目负责人
     */
    private Integer ownerUserId;

    /**
     * 负责人名称
     */
    private String ownerUserName;

}
