package com.bio.drqi.applet.dto.rsp;

import com.bio.drqi.applet.service.codescan.dto.BioResultInfoDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ScanCodeProjectSampleTestRspDTO {
    /**
     * 载体任务编码
     */
    private String vectorTaskCode;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 取样申请时间
     */
    private Date applyTime;

    /**
     * 取样申请人ID
     */
    private Integer applyUserId;

    /**
     * 取样申请人姓名
     */
    private String applyUserName;

    /**
     * 鉴定引物
     */
    private String testIdentifyPrimer;

    private String generation;

    private String speciesCode;

    private String breedCode;

    private String experimentNum;

    private String regionNum;

    private String seedNum;

    private String transformCode;

    /**
     * 来源
     */
    private String sourceCode;


    private OneTestResultInfo oneTestResultInfo;

    private List<BioResultInfoDTO> bioInfoList;


    @Data
    public class OneTestResultInfo {


        /**
         * 检测方法
         */
        private String testMethod;

        /**
         * 编辑类型
         */
        private String testEditType;

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
         * 检测数据递送人ID
         */
        private Integer testUserId;

        /**
         * 检测人
         */
        private String testUserName;

        /**
         * 检测时间
         */
        private String testTime;

        /**
         * 审核人姓名
         */
        private String checkUserName;

        /**
         * 审核人ID
         */
        private Integer checkUserId;

        /**
         * 审查结果
         */
        private String checkResult;

        /**
         * 创建日期
         */
        private Date createTime;

        /**
         * 更新日期
         */
        private Date updateTime;

        /**
         * 取样申请编号
         */
        private String applyNo;

        /**
         * 鉴定引物
         */
        private String identifyPrimer;

        /**
         * 唯一约束
         */
        private String uniqueCode;

        /**
         * 备注
         */
        private String remark;

        /**
         * 克隆苗
         */
        private String cloneSampleCode;



        /**
         * 检测原始结果
         */
        private String testOrgResult;


    }
}
