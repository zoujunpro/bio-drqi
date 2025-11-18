package com.bio.drqi.plant.dto.task;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PlantSampleTestTaskDTO {

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

    /**
     * 取消的数据
     */
    private String cancelTaskSampleList;


    @Data
    public static class RepeatSampleApply{

        private String sourceCode;

        /**
         * 种子编号
         */
        private String seedNum;

        /**
         * 实施方案编号
         */
        private String vectorTaskCode;

        /**
         * 物种编号
         */
        private String speciesCode;
        /**
         * 物种编号
         */
        private String speciesName;
        /**
         * 品种
         */
        private String breedName;

        /**
         * 品种
         */
        private String breedCode;
        /**
         * 代次编号
         */
        private String generationName;

    }

    @Data
    public static class FirstSampleApply {

        private String sourceCode;
        /**
         * 小区编号
         */
        private String regionNum;

        /**
         * 种子编号
         */
        private String seedNum;


        /**
         * 实施方案编号
         */
        private String vectorTaskCode;
        /**
         * 物种编号
         */
        private String speciesCode;

        /**
         * 品种code
         */
        private String breedCode;

        /**
         * 品种
         */
        private String breedName;




        @NotNull(message = "取样数量必填")
        private Integer sampleNum;

    }
}
