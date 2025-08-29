package com.bio.drqi.manage.sample.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SampleTestListDetailReqDTO extends PageDTO {


    private String applyNo;

    private Integer vectorTaskId;

    /**
     * 项目编码
     */
    private String projectCode;
    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 载体任务编码
     */
    private String vectorTaskCode;


}
