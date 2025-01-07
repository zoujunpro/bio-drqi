package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 取样检测申请表
 * @TableName cer_sample_apply_tb
 */
@TableName(value ="cer_sample_apply_tb")
@Data
public class CerSampleApplyTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 取样申请编号
     */
    private String applyNo;

    /**
     * 取样申请数量
     */
    private Integer applyNumber;

    /**
     * 取样申请时间
     */
    private Date applyTime;

    /**
     * 取样申请人ID
     */
    private Integer applyUserId;

    /**
     * 取样申请人
     */
    private String applyUserName;

    /**
     * 当前执行阶段 1待取样数据递送 2待检测结果递送，3取样结果审核完毕
     */
    private String currentStepCode;

    private String applyDesc;

    private String applyType;

    @TableField(exist = false)
    private String sampleCode;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}