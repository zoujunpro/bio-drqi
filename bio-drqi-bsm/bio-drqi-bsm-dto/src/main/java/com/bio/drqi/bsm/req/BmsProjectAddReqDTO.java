package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProjectAddReqDTO {
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编号
     */
    private String projectCode;
}
