package com.bio.drqi.tc.req;


import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TcPollinationExportPollinationExcelReqDTO {

    @NotBlank(message = "参数缺失：试验编号")
    private String experimentNum;

    private String sampleApplyNum;

    @NotEmpty(message = "缺失授粉内容")
    private List<Content> contentList;


    @Data
    @Valid
    public static class Content{

        private String matherRegionNum;
        /**
         * 母本种子编号
         */
        private String matherSeedNum;
        /**
         * 母本取样编号
         */

        private String matherSampleCode;

        /**
         * 母本大田取样编号
         */
        private String matherTcSampleCode;
        /**
         * 母本单株编号
         */
        private String matherSingleNumber;

        /**
         * 母本品种
         */
        private String matherBreedName;
        /**
         * 母本实施方案编号
         */
        private String matherVectorTaskCode;
        /**
         * 母本世代
         */
        private String matherGenerationName;
        /**
         * 母本基因类型
         */
        private String matherTcGene;
        /**
         * 父本小区编号
         */
        private String fatherRegionNum;
        /**
         * 父本种子编号
         */
        private String fatherSeedNum;

        /**
         * 父本大田取样编号
         */
        private String fatherTcSampleCode;
        /**
         * 父本单株编号
         */
        private String fatherSingleNumber;

        /**
         * 父本取样编号
         */
        private String fatherSampleCode;
        /**
         * 父本品种
         */
        private String fatherBreedName;

        /**
         * 父本实施方案编号
         */
        private String fatherVectorTaskCode;
        /**
         * 父本世代
         */
        private String fatherGenerationName;
        /**
         * 父本基因类型
         */
        private String fatherTcGene;

    }

}
