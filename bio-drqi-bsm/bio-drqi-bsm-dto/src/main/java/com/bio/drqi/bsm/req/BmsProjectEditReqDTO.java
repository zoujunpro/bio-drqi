package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProjectEditReqDTO {

    private Integer id;
    /**
     * 项目名称
     */
    private String projectName;

}
