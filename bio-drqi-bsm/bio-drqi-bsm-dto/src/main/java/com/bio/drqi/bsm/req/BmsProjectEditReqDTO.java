package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProjectEditReqDTO {

    private Integer id;
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 金蝶项目名称
     */
    private String kdProjectCode;

    /**
     * 金蝶项目编号
     */
    private String kdProjectName;

}
