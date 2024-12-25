package com.bio.drqi.manage.dto.project;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NewSampleTestDTO {

    /**
     * one 单管检测   more 96孔板检测
     */
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
     * 取样数据上送excel地址
     */
    private String sampleDataExcelUrl;

    /**
     * 检测数据上送地址
     */
    private String testDataExcelUrl;

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
        private String vectorTaskCode;
        private String sampleCode;
        private String identifyPrimer;
    }

    @Data
    public static class FirstSampleApply {

        /**
         * 预览 返显用
         */
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

    }

}
