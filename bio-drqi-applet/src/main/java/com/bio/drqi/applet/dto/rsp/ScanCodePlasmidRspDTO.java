package com.bio.drqi.applet.dto.rsp;

import lombok.Data;

@Data
public class ScanCodePlasmidRspDTO {

    private String projectCode;

    private String projectName;

    private String subProjectCode;

    private String subProjectName;


    private String vectorTaskCode;

    private String vectorTaskName;

    private CerVector cerVector;

    @Data
    public static class CerVector {

        private Integer id;

        /**
         * 载体构建任务
         */
        private Integer vectorTaskId;

        /**
         * 质粒名称
         */
        private String plasmidName;

        /**
         * 靶位点
         */
        private String targetSite;

        /**
         * 细菌抗性
         */
        private String bacterialResistance;

        /**
         * 质粒特异性引物
         */
        private String plasmidSpecificPrimers;

        /**
         * 细菌复制子
         */
        private String bacterialReplicon;

        /**
         * 拷贝数
         */
        private String copyNumber;

        /**
         * 农杆菌信息
         */
        private String agrobacteriumInformation;

        /**
         * 植物筛选标记
         */
        private String selectionMarker;

        /**
         * 外源基因
         */
        private String foreignGene;

        /**
         * 目标特性
         */
        private String geneCharacter;

        /**
         * 靶基因
         */
        private String targetGene;

        /**
         * PAM
         */
        private String pam;

        /**
         * 备注
         */
        private String remark;


        private String fileUrls;

        /**
         * 期望阳性苗
         */
        private Integer expectedPositiveVaccine;


        /**
         * 目的条带大小
         */
        private String destinationStripeSize;

        /**
         * 载体大小
         */
        private String vectorSize;

    }

}
