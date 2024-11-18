package com.bio.cer.project.req;

import lombok.Data;

import java.util.List;

@Data
public class CerConversionAndTransConfirmReqDTO {
    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 接收信息
     */
    private List<Content> contentList;


    @Data
    public static class Content {

        private String vectorTaskCode;

        private String sampleCode;

        private String transformCode;
        /**
         * 是否接收 Y N
         */
        private String dealResult;

        /**
         * 当接受时填写数量
         */
        private Integer acceptNum;
    }
}
