package com.bio.drqi.projectPrint;

import lombok.Data;

import java.util.List;

@Data
public class VectorBuildPrintReqDTO {

    private List<Content> contentList;
    @Data
    public static class Content{
        /**
         * 实施方案编号
         */
        private String vectorTaskCode;
        /**
         * 质粒名称
         */
        private String plasmidName;
        /**
         * 体积
         */
        private String capacity;

        /**
         * 浓度
         */
        private String concentration;
    }
}
