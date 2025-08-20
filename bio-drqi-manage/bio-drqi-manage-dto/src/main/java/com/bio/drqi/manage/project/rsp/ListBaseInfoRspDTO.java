package com.bio.drqi.manage.project.rsp;

import lombok.Data;

@Data
public class ListBaseInfoRspDTO {

    private Integer projectId;

    private String projectName;

    private String projectCode;
    /**
     * 编辑类型  1基因编辑 2转基因
     */
    private String geneEditMethod;


    private String projectStatus;

    private String projectType;
    /**
     * 项目预计开始日期
     */
    private String expectStartDate;



    private String projectCategoryCode;

}
