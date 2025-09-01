package com.bio.drqi.manage.project.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class ConversionAndTransDetailReqDTO extends PageDTO {

    /**
     * 取样编号
     */
    private String sampleCode;


    private String vectorTaskCode;

    private String subProjectCode;

    private String transformCode;

    /**
     * 项目编号
     */
    private String projectCode;


    private String plasmidName;

    private String taskNum;
}
