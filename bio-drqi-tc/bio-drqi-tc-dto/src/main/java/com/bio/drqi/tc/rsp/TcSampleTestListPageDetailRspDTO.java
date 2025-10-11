package com.bio.drqi.tc.rsp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TcSampleTestListPageDetailRspDTO {

    private Integer id;

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
     * 品种
     */
    private String breedCode;

    /**
     * 目标性状
     */
    private String targetCharacter;

    /**
     * 代次编号
     */
    private String generationCode;


    /**
     * 代次编号
     */
    private String generationName;


    /**
     * 田测基因型
     */
    private String tcGene;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 取样时间
     */
    private String sampleTime;

    /**
     * 申请编号
     */
    private String sampleApplyNum;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 鉴定引物
     */
    private String testIdentifyPrimer;

    /**
     * 检测方法
     */
    private String testMethod;

    /**
     * 编辑类型
     */
    private String testEditType;

    /**
     * 检测原始结果
     */
    private String testOrgResult;

    /**
     * 非转鉴定引物
     */
    private String testNoTransIdentityPrimer;

    /**
     * 是否为转基因阳性
     */
    private String testIsGeneModifyPositive;

    /**
     * 是否为定点插入
     */
    private String testIfFixedPoint;

    /**
     * 是否为单拷贝插入
     */
    private String testIfCopyInsert;

    /**
     * 定点插入方式（定点纯合/定点杂合）
     */
    private String testFixedPointType;

    /**
     * donor载体残留情况
     */
    private String testDonorResidueInfo;

    /**
     * 插入位点
     */
    private String testInsertionSite;

    /**
     * ELISA结果（蛋白表达量）
     */
    private String testElisaResult;

    /**
     * qbzr表达量
     */
    private String testQbzrSeq;

    /**
     * 编辑工具残留情况
     */
    private String testEditResidueInfo;

    /**
     * 检测时间
     */
    private String testTime;

    /**
     * 检测数据递送人ID
     */
    private Integer testUserId;

    /**
     * 检测人
     */
    private String testUserName;

    /**
     * 鉴定引物
     */
    private String identifyPrimer;

    /**
     * 审查结果  stay留苗   remove提苗
     */
    private String checkResult;

    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;


    private Integer matchNum;

    private String tcSampleCode;



    private List<BioInfoResult> bioInfoResultList = new ArrayList<>();


    @Data
    public static class BioInfoResult {
        private String sampleId;
        private String varType;
        private String mutate;
        private String ratio;
    }

    public void addBioInfoResultToList(String sampleId, String varType, String mutate, String ratio) {
        BioInfoResult bioInfoResult = new BioInfoResult();
        bioInfoResult.setSampleId(sampleId);
        bioInfoResult.setVarType(varType);
        bioInfoResult.setMutate(mutate);
        bioInfoResult.setRatio(ratio);
        this.bioInfoResultList.add(bioInfoResult);

    }

}
