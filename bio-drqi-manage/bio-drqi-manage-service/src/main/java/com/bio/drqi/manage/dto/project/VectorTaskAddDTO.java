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

    private String projectName;

    private String projectCode;

    private String subProjectCode;


    private String vectorTaskCode;


    private Integer vectorTaskId;
    /**
     * 载体构建信息，excel上传
     */
    private String vectorExcelUrl;

    /**
     * 载体的具体信息，需要区分转基因还是基因编辑
     */
    @Valid
    @NotNull(message = "载体信息缺失")
    private List<Vector> vectorList = new ArrayList<>();


    private TransportStart transportStart;

    private TransportEnd transportEnd;




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
         * 文件地址
         */
        private List<String> fileUrls;

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
