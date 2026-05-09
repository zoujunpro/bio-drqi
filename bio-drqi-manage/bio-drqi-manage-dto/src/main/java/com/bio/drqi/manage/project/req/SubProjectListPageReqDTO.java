package com.bio.drqi.manage.project.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SubProjectListPageReqDTO extends PageDTO {

    private Integer id;

    private String projectCode;


    /**
     * 子项目编码
     */
    private String subProjectCode;


    /**
     * 任务编号
     */
    private String taskNum;
}
