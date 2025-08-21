package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class VectorTaskAddDTO {

    private Integer vectorTaskId;

    /**
     * 任务word文件地址
     */
    private String wordUrl;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID必填")
    private Integer projectId;

    private String projectName;

    private String projectCode;


    private String subProjectCode;

    private String geneEditMethod;
    /**
     * 子项目ID
     */
    @NotNull(message = "子项目ID必填")
    private Integer subProjectId;
    /**
     * 载体构建任务编码
     */
    @NotBlank(message = "实施方案编码必填")
    private String vectorTaskCode;
    /**
     * 载体构建任务类型 1常任务创建 ，2瞬时测试 3原生质体 4发根
     */
    @EnumValue(strValues = {"1", "2", "3", "4"}, message = "实施方案编类型参数非法")
    private String vectorTaskType;

    @NotBlank(message = "物种必选")
    private String speciesCode;

    @NotBlank(message = "品种必填")
    private String breedCode;
    /**
     * 预计开始日期
     */
    @NotBlank(message = "预期开始时间必填")
    private String expectStartDate;

    /**
     * 预计结束日期
     */
    @NotBlank(message = "预期结束时间必填")
    private String expectEndDate;


    /**
     * 载体构建任务名称
     */
    @NotBlank(message = "子项目名称必填")
    private String vectorTaskName;
    /**
     * 建议递送方式
     */
    private String deliveryMethod;
    /**
     * 受体材料
     */
    @NotBlank(message = "受体材料必填")
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
     * 载体构建任务目标
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
     * 载体excel文件
     */
    private String vectorExcelUrl;

    /**
     * Y代表无质粒构建
     */
    private String plasmidFlag;


    private String  sampleCodePrefix;


    /**
     * 载体的具体信息，需要区分转基因还是基因编辑
     */
    @Valid
    @NotNull(message = "载体信息缺失")
    private List<Vector> vectorList = new ArrayList<>();
    /**
     * 共转质粒
     */
    @NotNull(message = "转化质粒缺失")
    private List<VectorGroup> vectorGroupList = new ArrayList<>();

    private List<ExcelVector> excelVectorList = new ArrayList<>();

    private TransportStart transportStart;

    private TransportEnd transportEnd;

    /**
     * 是否需要瞬时验证
     */
    private String noticeFlag;


    /**
     * 瞬时验证类型
     * 1原生质体、2发根、2其他瞬时任务
     */
    private String verifyType;


    /**
     * 其他手输入内容
     */
    private String otherHandInputContent;

    /**
     * 预估反应数
     */

    private String forecastReactionNumber;

    /**
     * 预计载体提交时间
     */
    private String forecastVectorSubmitTime;

    /**
     * 手写物种
     */
    private String handelSpeciesName;

    /**
     * 手写品种
     */
    private String handelBreedName;




    /**
     * 详情返显用
     */
    private String taskStatus;
    /**
     * 详情返显用
     */
    private Integer createUserId;



    @Data
    public static class TransportStart {
        /**
         * 发货地
         */
        private String deliveryLocation;

        /**
         * 发货日期
         */
        private String deliveryDate;

        /**
         * 运单号
         */
        private String expressNumber;

        /**
         * 快递公司名称
         */
        private String expressName;


        private String check1;

        private String check2;

        private String check3;

        private String check4;

        /**
         * 样品信息
         */
        private String sampleInfo;

        /**
         * 外层包装类型及数量
         */
        private String numDesc;

        /**
         * 备注
         */
        private String remark;

    }

    @Data
    public static class TransportEnd {
        /**
         * 收货地
         */
        private String receiptLocation;
        /**
         * 收货日期
         */
        private String receiptDate;
        /**
         * 包装是否完好
         */
        private String check1;
        /**
         * 数量是否匹配
         */
        private String check2;
        /**
         * 备注
         */
        private String remark;
    }


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


        /**
         * 浓度
         */
        private String concentration;


        /**
         * 体积
         */
        private String capacity;
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
