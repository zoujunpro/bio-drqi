package com.bio.drqi.tc.req;

import lombok.Data;

@Data
public class TcExperimentQueryByPdAndVectorTaskCodeReqDTO {
    private  String vectorTaskCode;

    private String pdImplementCode;
}
