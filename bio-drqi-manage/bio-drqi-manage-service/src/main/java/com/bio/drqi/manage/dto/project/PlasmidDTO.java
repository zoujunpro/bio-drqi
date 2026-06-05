package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PlasmidDTO{
    /**
     * 项目ID
     */
    @NotNull(message = "项目ID缺失")
    private Integer projectId;

    /**
     * 子项目ID
     */
    @NotNull(message = "子项目ID缺失")
    private Integer subProjectId;

    /**
     * 任务ID
     */
    @NotNull(message = "实施方案ID缺失")
    private Integer vectorTaskId;


    private String projectCode;

    private String projectName;

    private String subProjectCode;

    private String geneEditMethod;

    private String vectorTaskCode;

    private String vectorBuildRemark;

    @NotNull(message = "质检信息缺失")
    private List<Content> contentList;

    @Data
    @Valid
    public static class Content {


        @NotBlank(message = "参数缺失：plasmidName")
        @ExcelProperty(value = "质粒信息缺失")
        private String plasmidName;

        /**
         * 质检类型（下一步安排）1质粒制备 2农杆菌转化
         */
        @EnumValue(strValues = {"1", "2"}, message = "质检类型非法: 1质粒制备 2农杆菌转化")
        @ExcelProperty(value = "下一步安排")
        private String qualityInspectionType;
        /**
         * 质检结果
         */
        @EnumValue(strValues = {"refuse", "pass"}, message = "质检结果非法: 合格pass 不合格refuse")
        @ExcelProperty(value = "质检结果")
        private String qualityInspectionResult;
        /**
         * 质检农杆菌信息
         */
        @ExcelProperty(value = "农杆菌信息")
        private String agrobacteriumInformation;
        /**
         * 农杆菌抗性
         */
        @ExcelProperty(value = "农杆菌抗性")
        private String agrobacteriumResistance;
        /**
         * 质粒浓度
         */
        @ExcelProperty(value = "质粒浓度")
        private String plasmidConcentration;
        /**
         * 提取试剂盒
         */
        @ExcelProperty(value = "提取试剂盒")
        private String extractionKit;

        /**
         * 农杆菌储存位置
         */
        private String agrobacteriumLocation;

        /**
         * 农杆菌制备时间
         */
        @ExcelProperty(value = "农杆菌制备时间")
        private String makingDate;

        /**
         * 附件地址集合
         */
        private List<String> fileUrlList;

        private String remark;

        private String  updateFlag;

    }

}
