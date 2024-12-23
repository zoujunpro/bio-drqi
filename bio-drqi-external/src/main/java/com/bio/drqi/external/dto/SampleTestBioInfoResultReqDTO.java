package com.bio.drqi.external.dto;

import lombok.Data;

@Data
public class SampleTestBioInfoResultReqDTO {
    private String RunID;
    private String sampleID;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", RunID=").append(RunID);
        sb.append(", sampleID=").append(sampleID);
        sb.append("]");
        return sb.toString();
    }

}
