package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SeedDestructionPageReqDTO extends PageDTO {
    private String seedNum;
    private String taskNum;
    private String applyTaskNum;
    private Integer applyUserId;
    private String applyUserName;
    private String destructionLocation;
    private String destructionMethod;
    private String sourceType;
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
    private String productionLocationCode;
    private String stockLocationNum;
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
    /**
     * yyyyMMdd 销毁开始时间
     */
    private String beginDate;
    /**
     * yyyyMMdd 销毁结束时间
     */
    private String endDate;
}
