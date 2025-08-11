package com.bio.drqi.bsm.kd.dto;

import lombok.Data;

@Data
public class ExecuteBillQueryModelDTO {
    private String FormId;
    private String FieldKeys;
    private String FilterString;
}
