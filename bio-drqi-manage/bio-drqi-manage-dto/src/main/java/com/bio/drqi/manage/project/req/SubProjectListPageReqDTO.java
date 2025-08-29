package com.bio.drqi.manage.project.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class SubProjectListPageReqDTO extends PageDTO {

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
