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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}