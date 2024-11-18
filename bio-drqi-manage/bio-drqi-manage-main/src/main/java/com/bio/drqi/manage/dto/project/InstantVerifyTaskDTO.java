package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.cer.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class InstantVerifyTaskDTO {

    @NotBlank(message = "实施方案编号缺失")
    private String vectorTaskCode;

    private String geneEditMethod;


    @NotBlank(message = "物种必选")
    private String speciesCode;

    /**
     * 瞬时任务类型:1原生质体、2发根、2其他瞬时任务
     */
    @EnumValue(strValues = {"1", "2", "3"}, message = "瞬时任务类型非法")
    private String verifyType;

    @NotBlank(message = "瞬时测试编号缺少")
    private String verifyTaskCode;


    private String wordUrl;

    /**
     * 建议递送方式
     */
    private String deliveryMethod;
    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 建议编辑工具
     */
    private String editTools;
    /**
     * 工具类型
     */
    private String editToolsType;
    /**
     * 任务目标
     */
    private String vectorTaskTarget;
    /**
     * 备注（实验方案）
     */
    private String remark;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 预估反应数
     */

    private String forecastReactionNumber;

    /**
     * 预计反应时间+
     */
    private String forecastCompleteTime;

    private List<EstimatedTime> estimatedTimeList;

    /**
     * 载体excel文件
     */
    private String vectorExcelUrl;


    private List<ExcelVector> excelVectorList = new ArrayList<>();
    /**
     * 共转质粒
     */
    @NotNull(message = "转化质粒缺失")
    private List<VectorGroup> vectorGroupList = new ArrayList<>();

    @Data
    public static class EstimatedTime{

        /**
         * 事件类型  check experiment extract
         */
        private String eventType;

        /**
         * 预估开始时间
         */
        private String estimatedStartTime;

        /**
         * 预估结束时间
         */
        private String estimatedEndTime;


        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户名
         */
        private String userName;

        private Integer estimatedDate;

    }
    @Data
    public static class VectorGroup {
        private String groupName;
        private String plasmidNames;
        private String remark;
        private Integer repeatNum;

    }


    @Data
    public static class ExcelVector {

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


        /**
         * 浓度
         */
        private String concentration;


        /**
         * 体积
         */
        private String capacity;

    }

}
