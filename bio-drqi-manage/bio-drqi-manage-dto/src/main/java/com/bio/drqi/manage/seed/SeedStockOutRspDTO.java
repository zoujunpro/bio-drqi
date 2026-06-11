package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.BigDecimalSerialize;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeedStockOutRspDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 种子号
     */
    private String seedNum;

    /**
     * 用途code
     */
    private String useToCode;

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
     * 备注
     */
    private String remarks;

    /**
     * 出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 任务号
     */
    private String taskNum;

    /**
     * 出库号
     */
    private String outTaskNum;

    /**
     * 用途描述
     */
    private String useToDesc;

    private String plantCode;

    private String parentNum;

    private String fatherInfo;

    private String matherInfo;

    private String generation;

    private String speciesCode;

    private String breedCode;

    private String pollinationMethod;

    private String harvestType;

    private String harvestTime;

    private String sourceType;

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

    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal stockBeforeNumber;

    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal stockAfterNumber;

}
