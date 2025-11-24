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
 * @TableName bio_sample_apply_tb
 */
@TableName(value ="bio_sample_apply_tb")
@Data
public class BioSampleApplyTb implements Serializable {
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
     * 取样工单描述
     */
    private String applyDesc;

    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;

    /**
     * NGS测序文件地址
     */
    private String ngsExcelUrl;

    /**
     * 是否是克隆苗取样 Y N
     */
    private String cloneFlag;

    /**
     * 孔板类型 one,more
     */
    private String layoutFlag;

    /**
     * 实施方案编号
     */
    private String vectorTaskCodes;

    /**
     * 取样编号范围
     */
    private String sampleCodeRange;

    /**
     * 任务状态
     */
    private String taskStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}