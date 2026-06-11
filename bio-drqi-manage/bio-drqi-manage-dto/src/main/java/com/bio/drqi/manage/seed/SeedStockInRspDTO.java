package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.BigDecimalSerialize;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeedStockInRspDTO {
    private Integer id;

    /**
     * 上级种子编号
     */
    private String seedNum;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 计量单位g/kg/粒
     */
    private String unit;

    /**
     * 种子数量
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal seedNumber;

    /**
     * 种子来源（CER/温室/大田/外单位）
     */
    private String sourceType;

    /**
     * 申请任务编号
     */
    private String taskNum;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人姓名
     */
    private String applyUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String plantCode;

    private String parentNum;

    private String fatherInfo;

    private String matherInfo;

    private String generation;

    private String speciesCode;

    /**
     * 物种名称
     */
    private String speciesName;

    private String breedCode;

    /**
     * 品种名称
     */
    private String breedName;

    private String pollinationMethod;

    private String harvestType;

    private String harvestTime;

    private String productionLocationCode;

    private String stockLocationNum;

    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalNumber;

    private String targetCharacter;

    private String aliasName;

    private String geneType;

    private String materialType;

    private String matherSeedNum;

    private String fatherSeedNum;

    private String matherRegionNum;

    private String fatherRegionNum;

    private String genealogy;

    private String geneSeparateFlag;

    private String transFlag;

    private String vectorTaskCode;

    private String experimentNum;

    private String projectCode;

    private String fatherSingleNum;

    private String matherSingleNum;

    private String pdImplementCode;
}
