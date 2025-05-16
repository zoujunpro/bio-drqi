package com.bio.drqi.tc.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TcSampleTestTaskDTO {
    /**
     * 实验编号
     */
    private String experimentCode;

    /**
     * 取样组织
     */
    private String sampleOrganize;

    /**
     * 取样类型 首次取样 重复取样
     */
    private String applyType;

    /**
     * one 单管检测   more 96孔板检测
     */
    @NotBlank(message = "参数缺失：testType")
    private String testType;


    /**
     * 预计取样时间
     */
    private String expectedSampleTime;

    /**
     * 预计检测结果返回时间
     */
    private String expectedResultTime;

    /**
     * 首次取样申请
     */

    private List<FirstSampleApply> firstSampleApplyList;


    /**
     * 重复申请取样编号
     */
    private List<RepeatSampleApply> repeatSampleApplyList;

    /**
     * 重复取样excel
     */
    private String repeatSampleApplyExcelUrl;


    /**
     * 检测数据上送地址
     */
    private String testDataExcelUrl;


    private String identifyPrimerTemplateExcelUrl;


    private String bioInfoResultExcelUrl;


    @Data
    public static class RepeatSampleApply{
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
         * 品种编号
         */
        private String speciesCode;
        /**
         * 代次编号
         */
        private String generationCode;
        /**
         * 目标性状
         */
        private String targetCharacter;


        /**
         * 田测基因型
         */
        private String tcGene;

        private String sampleCode;
        /**
         * 取样时间
         */
        private String sampleTime;
    }

    @Data
    public static class FirstSampleApply {

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
         * 品种编号
         */
        private String speciesCode;

        /**
         * 代次编号
         */
        private String generationCode;

        /**
         * 田测基因型
         */
        private String tcGene;


        @NotNull(message = "取样数量必填")
        private Integer sampleNum;

        /**
         * 取样时间
         */
        private String sampleTime;


        private String targetCharacter ;
    }
}
