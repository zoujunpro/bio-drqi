package com.bio.drqi.manage.plasmid.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class PlasmidListPageReqDTO extends PageDTO {

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 载体任务编号
     */
    private String vectorTaskCode;

    /**
     * 质检人ID
     */
    private Integer createUserId;
}
