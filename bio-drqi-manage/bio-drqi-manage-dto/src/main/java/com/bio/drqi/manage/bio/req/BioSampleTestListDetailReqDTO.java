package com.bio.drqi.manage.bio.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BioSampleTestListDetailReqDTO extends PageDTO {


    private String applyNo;
    /**
     * 载体任务编码
     */
    private String vectorTaskCode;
    /**
     * 转化编号
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
    /**
     * 检测标识 Y
     */
    private String testFlag;

    private String checkResult;

    private String seedNum;

    private String regionNum;

    private String experimentNum;

}
