package com.bio.drqi.manage.project.rsp;

import lombok.Data;

import java.util.List;

@Data
public class SubProjectRspDTO {

    /**
     * 主键ID
     */
    private Integer id;

    private String subProjectName;
    /**
     * 子项目编码
     */
    private String subProjectCode;
    /**
     * 附件地址
     */
    private List<String> fileUrls;
    /**
     * 任务编号
     */
    private String taskNum;
    /**
     * 创建人姓名
     */
    private String createUserName;
    /**
     * 创建人Id
     */
    private Integer createUserId;

    private String taskStatus;

    private String priorityLevel;

    /**
     * 项目物种
     */
    private List<String> speciesList;
}
