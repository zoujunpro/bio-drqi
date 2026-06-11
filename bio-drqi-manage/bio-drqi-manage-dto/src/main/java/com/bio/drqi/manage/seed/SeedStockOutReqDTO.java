package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SeedStockOutReqDTO extends PageDTO {
    private String seedNum;

    /**seed_testing_apply 考种  seed_breed_apply繁种  seed_preparation_apply备种  seed_other_out_apply其他 seed_destruction_apply种子销毁  */
    private String useToCode;
    private String taskNum;
    private String outTaskNum;
    private String useToDesc;
    private String sourceType;
    private Integer applyUserId;
    private String applyUserName;
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
     * yyyyMMdd 出库开始时间
     */
    private String beginDate;
    /**
     * yyyyMMdd 出库结束时间
     */
    private String endDate;

}
