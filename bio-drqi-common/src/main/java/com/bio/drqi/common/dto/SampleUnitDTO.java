package com.bio.drqi.common.dto;

import com.bio.common.core.util.StringUtils;
import lombok.Data;

@Data
public class SampleUnitDTO {
    private String vectorTaskCode;
    private String transFormCode;
    private String sampleCode;
    private String uniqueId;
    private String identifyPrimer;
    private String regionNum;
    private String seedNum;
    private String tcSampleCode;

    public boolean ifNull() {
        return StringUtils.isEmpty(this.vectorTaskCode) && StringUtils.isEmpty(this.transFormCode) && StringUtils.isEmpty(this.sampleCode);
    }

    public SampleUnitDTO fillData(String vectorTaskCode, String transFormCode, String sampleCode, String identifyPrimer) {
        this.vectorTaskCode = vectorTaskCode;
        this.transFormCode = transFormCode;
        this.sampleCode = sampleCode;
        this.uniqueId = vectorTaskCode + sampleCode;
        this.identifyPrimer = identifyPrimer;
        return this;
    }

    public SampleUnitDTO(String vectorTaskCode, String transFormCode, String sampleCode, String identifyPrimer, String regionNum, String seedNum, String tcSampleCode)

    {
        this.vectorTaskCode = vectorTaskCode;
        this.transFormCode = transFormCode;
        this.sampleCode = sampleCode;
        this.uniqueId = vectorTaskCode + sampleCode;
        this.identifyPrimer = identifyPrimer;
        this.regionNum = regionNum;
        this.seedNum = seedNum;
        this.tcSampleCode = tcSampleCode;
    }

    public SampleUnitDTO() {
    }


}
