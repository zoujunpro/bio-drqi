package com.bio.drqi.manage.devOps;

import lombok.Data;

@Data
public class DevOpsModifyVectorTaskCodeReqDTO {
    private String oldVectorTaskCode;

    private String newVectorTaskCode;
}
