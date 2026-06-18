package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 
 * @TableName tc_harvest_seed_tb
 */
@TableName(value ="tc_harvest_seed_tb")
@Data
public class TcHarvestSeedTb {
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
     * 母本分子取样编号
     */
    private String mSampleCode;

    /**
     * 父本分子取样编号
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
     * 父本品种
     */
    private String fBreedCode;

    /**
     * 母本品种
     */
    private String mBreedCode;

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
    private String mTcGene;

    /**
     * 父本基因类型
     */
    private String fTcGene;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 授粉方式编号
     */
    private String pollinationMethodCode;


    /**
     * 收获方式编号
     */
    private String harvestTypeCode;


    /**
     * 收获时间
     */
    private String harvestTime;

    /**
     * 收获种子数量
     */
    private BigDecimal seedNumber;

    /**
     * 计量单位g/kg/粒 
     */
    private String unit;

    /**
     * 收获申请工单
     */
    private String harvestApplyNum;

    /**
     * 收获备注
     */
    private String remark;

    /**
     * 父本单株编号
     */
    private String fSingleNumber;

    /**
     * 母本单株编号
     */
    private String mSingleNumber;

    /**
     * 母本大田取样编号
     */
    private String mTcSampleCode;

    /**
     * 父本大田取样编号
     */
    private String fTcSampleCode;

    /**
     * 种子编号
     */
    private String seedNums;

    private String materialType;
}