package com.bio.drqi.manage.sample.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BioInfoPageReqDTO extends PageDTO {

    private String applyNo;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 载体任务编码
     */
    private String vectorTaskCode;


    /**
     * 转化编号/种子编号
     */
    private String transformCode;

    /**
     * 取样编号
     */
    private String sampleCode;
}
