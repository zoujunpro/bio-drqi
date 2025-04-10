package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProjectQueryAllReqDTO {

    /**
     * 项目名称
     */
    private String projectCode;

    /**
     * 项目编号
     */
    private String projectName;
}
