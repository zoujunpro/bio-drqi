package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class VectorBuildAddDTO {

    private Integer vectorTaskId;

    /**
     * 载体excel文件
     */
    private String vectorExcelUrl;


    /**
     * 载体的具体信息，需要区分转基因还是基因编辑
     */
    private List<Vector> VectorList = new ArrayList<>();
    /**
     * 共转质粒
     */
    private List<VectorGroup> vectorGroupList = new ArrayList<>();

    private List<ExcelVector> excelVectorList;


    @Data
    public static class VectorGroup {
        private String groupName;
        private String plasmidNames;
        private String remark;
        private Integer repeatNum;

    }

    /***
     * 基因编辑
     */
    @Data
    public static class Vector {


        /**
         * 质粒名称
         */
        @NotBlank(message = "质粒名称必填")
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
         * 目标特性
         */
        private String geneCharacter;

        /**
         * 备注
         */
        private String remark;

        /**
         * 期望阳性苗
         */
        private Integer expectedPositiveVaccine;

        /**
         * 文件地址
         */
        private List<String> fileUrls;

        /**
         * 外源基因(转基因专有)
         */
        private String foreignGene;
        /**
         * 靶基因（基因编辑专有）
         */
        private String targetGene;

        /**
         * PAM（基因编辑专有）
         */
        private String pam;
    }

    @Data
    public static class ExcelVector{

        /**
         * 编号
         */
        @ExcelProperty("编号")
        private String num;
        /**
         * 质粒名称
         */
        @ExcelProperty("质粒名称")
        private String plasmidName;
        /**
         * 细菌抗性
         */
        @ExcelProperty("细菌抗性")
        private String bacterialResistance;


        /**
         * 质粒特异性引物
         */
        @ExcelProperty("质粒特异性鉴定引物")
        private String plasmidSpecificPrimers;

        /**
         * 目的条带大小
         */
        @ExcelProperty("目的条带大小")
        private String destinationStripeSize;

        /**
         * 载体大小
         */
        @ExcelProperty("载体大小（Kb）")
        private String vectorSize;
        /**
         * 拷贝数
         */
        @ExcelProperty("拷贝数（高/中/低）")
        private String copyNumber;

        /**
         * 植物筛选标记
         */
        @ExcelProperty("植物筛选标记（如有请填写）")
        private String selectionMarker;

        /**
         * 备注
         */
        @ExcelProperty("备注")
        private String remark;

    }


}
