package com.bio.drqi.manage.devOps;

import lombok.Data;

@Data
public class DevOpsModifySubProjectCodeReqDTO {
    private String oldSubProjectCode;

    private String newSubProjectCode;
}
