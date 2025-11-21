package com.bio.drqi.manage.dto.plant.task;

import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PlantSampleTestTaskDTO {


    /**
     * 物种编号
     */
    private String speciesCode;

    /**
     * 取样类型 首次取样 重复取样
     */
    @EnumValue(message = "取样类型参数错误", strValues = {"F", "R"})
    private String applyType;

    /**
     * one 单管检测   more 96孔板检测
     */
    @EnumValue(message = "参数缺失：testType", strValues = {"one", "more"})
    private String testType;


    /**
     * 首次取样申请
     */

    private List<FirstSampleApply> firstSampleApplyList;


    /**
     * 重复申请取样编号
     */
    private List<RepeatSampleTest> repeatSampleTestList;

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
    public static class RepeatSampleTest {

        private String sourceCode;

        private String sampleCode;

        private String breedCode;

        private String breedName;

        private String speciesCode;

        private String speciesName;

        private String vectorTaskCode;

        private String regionNum;

        private String seedNum;
    }

    @Data
    public static class FirstSampleApply {


        private String sourceCode;
        /**
         * CER试验编号
         */
        private String plantExperimentNum;
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
         * 实施方案编号
         */
        private String transformCode;


        @NotNull(message = "取样数量必填")
        private Integer sampleNumber;

    }
}
