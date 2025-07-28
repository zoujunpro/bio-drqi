package com.bio.drqi.tc.rsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
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
    @JsonProperty("mRegionNum")
    private String mRegionNum;

    /**
     * 父本小区编号
     */
    @JsonProperty("fRegionNum")
    private String fRegionNum;

    /**
     * 母本单株编号
     */
    @JsonProperty("mSampleCode")
    private String mSampleCode;

    /**
     * 父本单株编号
     */
    @JsonProperty("fSampleCode")
    private String fSampleCode;

    /**
     * 母本种子编号
     */
    @JsonProperty("mSeedNum")
    private String mSeedNum;

    /**
     * 父本种子编号
     */
    @JsonProperty("fSeedNum")
    private String fSeedNum;

    /**
     * 父本品种
     */
    @JsonProperty("fBreedName")
    private String fBreedName;

    /**
     * 父本品种
     */
    @JsonProperty("fBreedCode")
    private String fBreedCode;

    /**
     * 母本品种
     */
    @JsonProperty("mBreedName")
    private String mBreedName;

    /**
     * 母本品种
     */
    @JsonProperty("mBreedCode")
    private String mBreedCode;


    /**
     * 母本实施方案编号
     */
    @JsonProperty("mVectorTaskCode")
    private String mVectorTaskCode;

    /**
     * 父本实施方案编号
     */
    @JsonProperty("fVectorTaskCode")
    private String fVectorTaskCode;

    /**
     * 母本世代
     */
    @JsonProperty("mGenerationCode")
    private String mGenerationCode;

    /**
     * 父本世代
     */
    @JsonProperty("fGenerationCode")
    private String fGenerationCode;

    /**
     * 母本基因类型
     */
    @JsonProperty("mTcGene")
    private String mTcGene;

    /**
     * 父本基因类型
     */
    @JsonProperty("fTcGene")
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


    private String unit;

    private BigDecimal seedNumber;
}
