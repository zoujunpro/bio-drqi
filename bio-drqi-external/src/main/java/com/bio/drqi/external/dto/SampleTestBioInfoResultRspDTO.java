package com.bio.drqi.external.dto;

import lombok.Data;

@Data
public class SampleTestBioInfoResultRspDTO {
    private String Unique_DB_code;
    private String RunID;
    private String sampleID;
    private String HapID;
    private String vartype;
    private String mutate;
    private String ratio;
}
