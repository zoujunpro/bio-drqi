package com.bio.drqi.manage.project.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class SubProjectListPageRspDTO {
    private Integer id;

    private String projectCode;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 附件地址
     */
    private String fileUrls;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 1审批中 2审批通过，3审批拒绝
     */
    private String taskStatus;

    private String priorityLevel;

    private String speciesCode;
}
