package com.bio.drqi.manage.dto.project;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConversionAndTransDTO {


    /**
     * 交接日期
     */
    private String handoverDate;


    /**
     * 图片地址
     */
    private List<String> imageUrlList;


    private List<SampleCode> sampleCodeList = new ArrayList<>();

    private List<TransForm> transFormList = new ArrayList<>();

    private String excelUrl;

    private Integer totalNum;

    private String remark;

    @Data
    public static class TransForm {
        private String vectorTaskCode;

        private String transformCode;

        private String acceptorMaterial;

        private Integer transNum;

        /**
         * 是否转基因 Y-是,N-否 O-N/A
         */
        private String transGeneFlag;

        private String plasmidName;

        /**
         * 是否接收 Y N
         */
        private String dealResult;

        /**
         * 确认接收数量
         */
        private Integer acceptNum;

        private String remark;
    }


    @Data
    public static class SampleCode {

        private String vectorTaskCode;

        private String sampleCode;
        /**
         * 是否编辑纯合 Y,N
         */
        private String editPureUnion;

        private String acceptorMaterial;

        /**
         * 是否转基因 Y-是,N-否 O-N/A
         */
        private String transGeneFlag;

        private String plasmidName;

        /**
         * 是否接收 Y N
         */
        private String dealResult;

        private String remark;

    }


}
