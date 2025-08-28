package com.bio.drqi.manage.transform.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class TransformListPageReqDTO extends PageDTO {
    /**
     * 载体任务编号
     */
    private String vectorTaskCode;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 创建人ID
     */
    private Integer createUserId;
    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 质粒名称
     */
    private String plasmidName;

    private String speciesCode;


}
