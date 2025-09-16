package com.bio.drqi.manage.vector.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class VectorListPageRspDTO {
    private Integer id;

    /**
     * 载体构建任务编码
     */
    private String vectorTaskCode;

    /**
     * 载体构建任务类型 1测试任务（普通），2测试任务（原生质体），3正常任务创建
     */
    private String vectorTaskType;


    /**
     * 递送方式  1基因枪、2原生质体转化、3农杆菌转化、4病毒载体
     */
    private String deliveryMethod;

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 编辑类型  1 KO，2点突变，3精准小，4精准大
     */
    private String editType;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 任务状态 1审批中 2审批通过，3审批拒绝
     */
    private String taskStatus;

    /**
     * 质质检结果  空未质检 pass已通过 refuse未通过
     */
    private String qualityInspectionResult;


    private String speciesCode;

    private String speciesName;

    private String breedCode;

    private String breedName;

    private String currentStepCode;

    private String currentStepName;

    /**
     * 期望阳性苗
     */
    private String expectedPositiveSeed;

    /**
     * 监管级别
     */
    private String supervisionLevelCode;


    private String vectorBuildFlag;
    /**
     * 预计开始日期
     */
    private String expectStartDate;

    /**
     * 预计结束日期
     */
    private String expectPeriod;


    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;


    private String noPlasmidFlag;

}
