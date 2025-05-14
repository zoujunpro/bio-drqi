package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName tc_pollination_tb
 */
@TableName(value ="tc_pollination_tb")
@Data
public class TcPollinationTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 取样批次号
     */
    private String sampleApplyNum;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 母本小区编号
     */
    private String mRegionNum;

    /**
     * 父本小区编号
     */
    private String fRegionNum;

    /**
     * 母本单株编号
     */
    private String mSampleCode;

    /**
     * 父本单株编号
     */
    private String fSampleCode;

    /**
     * 母本种子编号
     */
    private String mSeedNum;

    /**
     * 父本种子编号
     */
    private String fSeedNum;

    /**
     * 品种
     */
    private String speciesCode;

    /**
     * 母本实施方案编号
     */
    private String mVectorTaskCode;

    /**
     * 父本实施方案编号
     */
    private String fVectorTaskCode;

    /**
     * 母本世代
     */
    private String mGenerationCode;

    /**
     * 父本世代
     */
    private String fGenerationCode;

    /**
     * 母本基因类型
     */
    private String mGeneType;

    /**
     * 父本基因类型
     */
    private String fGeneType;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 授粉方式编号
     */
    private String pollinationMethodCode;

    /**
     * 授粉方式名称
     */
    private String pollinationMethodName;

    /**
     * 收获方式编号
     */
    private String harvestTypeCode;

    /**
     * 收获方式名称
     */
    private String harvestTypeName;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}