package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 载体构建任务
 * @TableName cer_vector_task_tb
 */
@TableName(value ="cer_vector_task_tb")
@Data
public class CerVectorTaskTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /**
     * 载体构建任务编码
     */
    private String vectorTaskCode;


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



    private String speciesCode;

    private String breedCode;

    private String currentStepCode;

    /**
     * 期望阳性苗
     */
    private String expectedPositiveSeed;

    /**
     * 监管级别
     */
    private String supervisionLevelCode;

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


    private String vectorBuildRemark;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}