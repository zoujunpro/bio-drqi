package com.bio.drqi.applet.dto.req;


import lombok.Data;

@Data
public class QueryByPlantCodeReqDTO {

    private String plantCode;

    private String vectorTaskCode;
}
