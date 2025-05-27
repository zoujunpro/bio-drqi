package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName tc_sample_test_apply_tb
 */
@TableName(value ="tc_sample_test_apply_tb")
@Data
public class TcSampleTestApplyTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 申请编号
     */
    private String sampleApplyNum;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 取样组织
     */
    private String sampleOrganize;

    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;


    private String testType;

    /**
     * 预计取样时间
     */
    private String expectedSampleTime;

    /**
     * 预计检测结果返回时间
     */
    private String expectedResultTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名字
     */
    private String createUserName;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 测序类型 1 NGS测序  2一代测序
     */
    private String sequenceType;

    /**
     *测序结果文件
     */
    private String resultExcelUrl;

    /**
     * 孔板引物排版文件
     */
    private String identifyPrimerExcelUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}