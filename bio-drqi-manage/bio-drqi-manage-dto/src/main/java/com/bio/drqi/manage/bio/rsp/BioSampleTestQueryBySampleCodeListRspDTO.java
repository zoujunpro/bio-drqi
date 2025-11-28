package com.bio.drqi.manage.bio.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BioSampleTestQueryBySampleCodeListRspDTO {

    /**
     * 载体任务编码
     */
    private String vectorTaskCode;

    /**
     * 取样编号
     */
    private String sampleCode;
    /**
     * 来源
     */
    private String sourceCode;

    private String generation;

    private String speciesCode;

    private String speciesName;

    private String breedCode;

    private String breedName;

    private String experimentNum;

    private String regionNum;

    private String seedNum;

    private String cloneSeedNum;
}
