package com.bio.drqi.tc;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class SampleUnitDTO {
    private String vectorTaskCode;
    private String transFormCode;
    private String sampleCode;
    private String uniqueId;
    private String identifyPrimer;

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
    public SampleUnitDTO(String vectorTaskCode, String transFormCode, String sampleCode,String identifyPrimer) {
        this.vectorTaskCode = vectorTaskCode;
        this.transFormCode = transFormCode;
        this.sampleCode = sampleCode;
        this.uniqueId = vectorTaskCode + sampleCode;
        this.identifyPrimer = identifyPrimer;
    }

    public SampleUnitDTO() {
    }


}
