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
    private String experimentNum;

    /**
     * 取样组织
     */
    private String sampleOrganize;

    /**
     * 取样类型 首次取样 重复取样
     */
    private String applyType;

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
        private String vectorTaskCode;
        private String sampleCode;
        private String identifyPrimer;
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
         * 目标性状
         */
        private String targetCharacter;

        /**
         * 代次编号
         */
        private String generationCode;




    }
}
