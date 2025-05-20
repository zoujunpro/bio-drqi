package com.bio.drqi.tc.rsp;

import lombok.Data;

@Data
public class TcSampleTestQueryListBySampleCodeListRspDTO {
    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种编号
     */
    private String speciesCode;


    /**
     * 品种
     */
    private String breedName;

    /**
     * 目标性状
     */
    private String targetCharacter;

    /**
     * 代次编号
     */
    private String generationCode;

    /**
     * 田测基因型
     */
    private String tcGene;

    /**
     * 取样编号
     */
    private String sampleCode;


}
