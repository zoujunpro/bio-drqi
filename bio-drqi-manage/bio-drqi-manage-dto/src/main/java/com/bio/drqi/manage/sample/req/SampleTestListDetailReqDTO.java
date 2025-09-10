package com.bio.drqi.manage.sample.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SampleTestListDetailReqDTO extends PageDTO {


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


    /**
     * 是否是克隆苗取样 Y克隆苗取样 N非克隆苗取样
     */
    private String cloneSampleFlag;


    private String cloneSampleCode;

    private String targetFlag;

}
