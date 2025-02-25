package com.bio.drqi.applet.dto.req;


import lombok.Data;

@Data
public class QueryBySampleCodeReqDTO {
    private String sampleCode;

    private String vectorTaskCode;
}
