package com.bio.drqi.transform.req;

import com.bio.drqi.base.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TransformListByVectorTaskRspDTO extends PageDTO {


    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 载体任务ID
     */
    private Integer vectorTaskId;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

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
     * 侵染数量
     */
    private Integer infectNumber;

    /**
     * 侵染日期
     */
    private String infectDate;

    /**
     * 递送方式（实际使用的方式）
     */
    private String deliveryMethod;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;


    /**
     * 创建人名称
     */
    private String createUserName;



    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 任务状态 1审批中 2审批通过，3审批拒绝
     */
    private String taskStatus;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 质粒名称
     */
    private String plasmidName;
}
