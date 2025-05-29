package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 田测实验表
 * @TableName tc_experiment_tb
 */
@TableName(value ="tc_experiment_tb")
@Data
public class TcExperimentTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 项目编号
     */
    private String projectCodes;

    /**
     * 实施方案编号
     */
    private String vectorTaskCodes;

    /**
     * 物种编码
     */
    private String speciesCode;

    /**
     * 物种名称
     */
    private String speciesName;

    /**
     * 上传附件
     */
    private String fileUrl;

    /**
     * 实验目的
     */
    private String experimentGoal;

    /**
     * 实验地点
     */
    private String experimentAddress;

    /**
     * 申请人
     */
    private String applyUserName;

    /**
     * 申请人iD
     */
    private Integer applyUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 任务编号
     */
    private String taskNum;

    private String designUrl;

    private String sampleCodePrefix;

    private Integer nextSampleNumber;

    private String overFlag;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}