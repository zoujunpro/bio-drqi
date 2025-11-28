package com.bio.drqi.manage.dto.project;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Data
public class NewSampleTestDTO {

    /**
     * one 单管检测   more 96孔板检测
     */
    @NotBlank(message = "参数缺失：testType")
    private String testType;

    @NotBlank(message = "参数缺失：speciesCode")
    private String speciesCode;

    /**
     * 是否是克隆苗
     */
    private boolean cloneFlag;



    private String cancelTaskSampleList;

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
     * 取样数据上送excel地址
     */
    private String sampleDataExcelUrl;

    /**
     * 检测数据上送地址
     */
    private String testDataExcelUrl;


    private String identifyPrimerTemplateExcelUrl;


    private String bioInfoResultExcelUrl;

    /**
     * 1发起取样申请提交
     * 2取样结果上送提交
     * 3检测结果上送提交
     * 4取样检测结果审核提交
     */
    private String operateType;









    @Data
    public static class RepeatSampleApply{

        @NotNull(message = "实施方案编号缺失")
        private String vectorTaskCode;

        @NotNull(message = "取样编号缺失")
        private String sampleCode;

        private String identifyPrimer;
        /**
         * 克隆苗
         */
        @NotNull(message = "克隆苗数量缺失")
        private Integer cloneSeedNum;

        private Integer cloneNum;

        private String breedCode;
        private String breedName;
        private String speciesCode;
        private String speciesName;
    }

    @Data
    public static class FirstSampleApply {

        /**
         * 预览 返显用
         */
        @NotBlank(message = "实施方案缺失")
        private String vectorTaskCode;

        /**
         * 预览 返显用
         */
        private String acceptorMaterial;

        /**
         * 预览 返显用
         */
        private String plasmidName;


        @NotBlank(message = "转化编号缺失")
        private String transformCode;

        @NotNull(message = "取样数量必填")
        private Integer sampleNum;


        private String identifyPrimer;
        /**
         * 取样时间
         */
        private String sampleTime;
        /**
         * 代次
         */
        private String sampleGeneration;

    }

}
