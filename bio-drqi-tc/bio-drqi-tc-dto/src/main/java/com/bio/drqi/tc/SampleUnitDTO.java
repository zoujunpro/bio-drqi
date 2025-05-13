package com.bio.drqi.tc;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class SampleUnitDTO {
    private String vectorTaskCode;
    private String experimentCode;
    private String regionNum;
    private String seedNum;
    private String sampleCode;
    private String uniqueId;
    private String identifyPrimer;

    public boolean ifNull() {
        return StringUtils.isEmpty(this.vectorTaskCode) && StringUtils.isEmpty(this.seedNum) && StringUtils.isEmpty(this.sampleCode)&&StringUtils.isEmpty(this.regionNum)&&StringUtils.isEmpty(this.experimentCode);
    }
    public SampleUnitDTO fillData(String vectorTaskCode, String experimentCode,String regionNum,String seedNum, String sampleCode, String identifyPrimer) {
        this.vectorTaskCode = vectorTaskCode;
        this.experimentCode = experimentCode;
        this.sampleCode = sampleCode;
        this.uniqueId = vectorTaskCode + sampleCode;
        this.identifyPrimer = identifyPrimer;
        this.regionNum=regionNum;
        this.seedNum=seedNum;
        return this;
    }
    public SampleUnitDTO(String vectorTaskCode, String experimentCode,String regionNum,String seedNum, String sampleCode, String identifyPrimer) {
        this.vectorTaskCode = vectorTaskCode;
        this.experimentCode = experimentCode;
        this.regionNum=regionNum;
        this.seedNum=seedNum;
        this.sampleCode = sampleCode;
        this.uniqueId = vectorTaskCode + sampleCode;
        this.identifyPrimer = identifyPrimer;
    }

    public SampleUnitDTO() {
    }


}
