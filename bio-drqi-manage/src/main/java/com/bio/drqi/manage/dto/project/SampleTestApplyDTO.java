package com.bio.drqi.manage.dto.project;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SampleTestApplyDTO {


    /**
     * 正常取样
     */

    private List<Content> contentList;


    /**
     * 重复申请取样编号
     */
    private List<RepeatContent> repeatContentList;


    @Data
    public static class RepeatContent{
            private String vectorTaskCode;
            private String sampleCode;
            private String identifyPrimer;
    }

    @Data
    public static class Content {

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
