package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.BigDecimalSerialize;
import com.bio.drqi.manage.base.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SeedDestructionPageRspDTO extends PageDTO {

    private Integer id;

    /**
     * 销毁地点
     */
    private String destructionLocation;

    /**
     * 种子号
     */
    private String seedNum;

    /**
     * 销毁方法
     */
    private String destructionMethod;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数量
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal seedNumber;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 申请任务编号
     */
    private String applyTaskNum;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 销毁证据
     */
    private List<String> destructionEvidenceList;


    /**
     * 执行状态1执行中 2已执行
     */
    private String status;

    /***
     * 申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyDate;

    /***
     * 销毁时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date destructionDate;

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
