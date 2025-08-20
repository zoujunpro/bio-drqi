package com.bio.drqi.manage.project.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProjectListRspDTO {
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目编号
     */
    private String projectCode;
    /**
     * 项目类型
     */
    private String projectType;
    /**
     * 立项时间
     */
    private Date projectTime;

    /**
     * 项目状态   approve审批中 , execute执行中，stop暂停，complete完成
     */
    private String projectStatus;
    /**
     * 项目创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    /**
     * 项目更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
    /**
     * 项目创建人
     */
    private Integer createUserId;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 编辑类型  1基因编辑 2转基因
     */
    private String geneEditMethod;
    /**
     * 项目预计开始日期
     */
    private String expectStartDate;

    private String taskNum;

    private String currentStepName;

    private String currentStepCode;

    private int childrenNum;

    private String projectCategoryCode;

    /**
     * 项目负责人
     */
    private Integer ownerUserId;

    /**
     * 负责人名称
     */
    private String ownerUserName;


}
