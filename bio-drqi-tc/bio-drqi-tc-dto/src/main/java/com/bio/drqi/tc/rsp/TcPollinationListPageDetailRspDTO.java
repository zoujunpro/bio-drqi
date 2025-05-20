package com.bio.drqi.tc.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class TcPollinationListPageDetailRspDTO {
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
     * 父本品种
     */
    private String fBreedName;

    /**
     * 母本品种
     */
    private String mBreedName;

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
    private String fGenerationName;

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
}
