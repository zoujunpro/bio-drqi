package com.bio.drqi.tc.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcExperimentQueryByPdAndVectorTaskCodeReqDTO {

    private String vectorTaskCode;

    private String pdImplementCode;

    @NotBlank(message = "试验类型入参缺失")
    private String experimentType;
}
