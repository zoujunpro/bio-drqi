package com.bio.drqi.manage.dto.seed;

import com.bio.drqi.manage.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SeedInStoreDTO extends SeedProcDTO {

    /**
     * 申请表单
     */
    private ApplyForm applyForm;

    /**
     * 执行表单
     */
    private ExecuteForm executeForm;

    @Data
    public static class ApplyForm {


        private List<ApplyFromContent> applyFromContentList;


    }

    @Data
    private static  class ApplyFromContent{

        /**
         * 发货地址
         */
        private String applyAddress;

        /**
         * 作物
         */
        private String crop;

        /**
         * 数量
         */
        private String num;

        /**
         * 预计到货时间
         */
        private String expectArrivalTime;
    }

    @Data
    public static class ExecuteForm {

        private String excelUrl;

        private List<ExecuteFormContent> executeFormContentList = new ArrayList<>();

    }

    @Data
    public static class ExecuteFormContent {
        /**
         * 来源
         */
        private String source;

        /**
         * 上一代种子编号
         */
        private String parentNum;
        /**
         * 种植编号
         */
        private String plantNum;
        /**
         * 取样编号
         */
        private String projectCode;
        /**
         * 取样编号
         */
        private String sampleCode;
        /**
         * 父本信息
         */
        private String fartherInfo;

        /**
         * 母本信息
         */
        private String matherInfo;
        /**
         * 代次
         */
        private String generation;
        /**
         * 项目物种
         */
        @NotBlank(message = "参数缺失：speciesCode")
        private String speciesCode;

        private String speciesName;
        /**
         * 受体材料（作物）
         */
        @NotBlank(message = "参数缺失：breedCode")
        private String breedCode;

        private String breedName;

        /**
         * 授粉方式
         */
        private String pollinationMethod;
        /**
         * 种子类型
         */
        private String seedType;
        /**
         * 收获方式，单珠和混珠
         */
        private String harvestType;
        /**
         * 收获时间
         */
        private String harvestTime;
        /**
         * 种子数量
         */
        @NotNull(message = "参数缺失：seedNumber")
        private BigDecimal seedNumber;
        /**
         * 计量单位g/kg/粒
         */
        @EnumValue(strValues = {"g", "kg", "粒"}, message = "参数非法：unit")
        private String unit;
        /**
         * 生产地点（天津/海南/新乡）
         */
        private String productionLocationName;

        /**
         * 基因型性状
         */
        private String geneticCharacter;

        /**
         * 备注
         */
        private String remarks;

        private String stockLocationNum;

        private String geneType;


        private String aliasName;

        private String materialType;

        private String storeFlag;

        @NotBlank(message = "参数缺失：uniqueCode")
        private String uniqueCode;



    }


}
