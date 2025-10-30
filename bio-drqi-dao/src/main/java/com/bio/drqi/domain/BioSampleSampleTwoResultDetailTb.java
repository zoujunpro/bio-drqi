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
 * @TableName bio_sample_sample_two_result_detail_tb
 */
@TableName(value ="bio_sample_sample_two_result_detail_tb")
@Data
public class BioSampleSampleTwoResultDetailTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 材料名称
     */
    private String sampleId;

    /**
     * 生信系统唯一编号
     */
    private String uniqueDbCode;

    /**
     * 测序编号
     */
    private String runId;

    /**
     * HapID
     */
    private String hapId;

    /**
     * 变异类型合计
     */
    private String varType;

    /**
     * 突变方向合计
     */
    private String mutate;

    /**
     * 变异类型占比(%)
     */
    private String ratio;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 生信分析结果确认状态 checked  none 
     */
    private String confirmStatus;

    /**
     * 分析编号
     */
    private String resultKey;

    /**
     * 匹配 Y N
     */
    private String matchFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}