package com.bio.drqi.tc;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class SampleUnitDTO {
    private String vectorTaskCode;
    private String experimentNum;
    private String regionNum;
    private String seedNum;
    private String sampleCode;
    private String tcSampleCode;
    private String uniqueId;
    private String identifyPrimer;

    public boolean ifNull() {
        return StringUtils.isEmpty(this.vectorTaskCode) && StringUtils.isEmpty(this.seedNum) && StringUtils.isEmpty(this.sampleCode)&&StringUtils.isEmpty(this.regionNum)&&StringUtils.isEmpty(this.experimentNum);
    }
    public SampleUnitDTO fillData(String vectorTaskCode, String experimentNum,String regionNum,String seedNum, String sampleCode, String identifyPrimer,String tcSampleCode) {
        this.vectorTaskCode = vectorTaskCode;
        this.experimentNum = experimentNum;
        this.sampleCode = sampleCode;
        this.uniqueId = vectorTaskCode + sampleCode;
        this.identifyPrimer = identifyPrimer;
        this.regionNum=regionNum;
        this.seedNum=seedNum;
        this.tcSampleCode=tcSampleCode;
        return this;
    }
    public SampleUnitDTO(String vectorTaskCode, String experimentNum,String regionNum,String seedNum, String sampleCode, String identifyPrimer,String tcSampleCode) {
        this.vectorTaskCode = vectorTaskCode;
        this.experimentNum = experimentNum;
        this.regionNum=regionNum;
        this.seedNum=seedNum;
        this.sampleCode = sampleCode;
        this.uniqueId = vectorTaskCode + sampleCode;
        this.identifyPrimer = identifyPrimer;
        this.tcSampleCode=tcSampleCode;
    }

    public SampleUnitDTO() {
    }


}
