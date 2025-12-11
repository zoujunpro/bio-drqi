package com.bio.drqi.manage.devOps;

import lombok.Data;

@Data
public class DevOpsModifyProjectCodeReqDTO {
    private String oldProjectCode;

    private String newProjectCode;
}
