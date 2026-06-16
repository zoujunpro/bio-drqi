package com.bio.drqi.manage.vector.rsp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 实施方案全量展示信息。
 */
@Data
public class CerImplementationPlanFullInfoRspDTO {

    /**
     * 实施方案基础信息
     */
    private CerImplementationPlanBaseInfoRspDTO planInfo;

    /**
     * 质粒/引物信息
     */
    private List<PlasmidPrimerInfo> plasmidPrimerList = new ArrayList<>();

    /**
     * 转化-取样-种子库信息
     */
    private List<TransformSampleSeedInfo> transformSampleSeedList = new ArrayList<>();

    @Data
    public static class PlasmidPrimerInfo {
        /**
         * 质粒/载体名称
         */
        private String plasmidName;

        /**
         * 引物
         */
        private String primer;


    }

    @Data
    public static class TransformSampleSeedInfo {
        /**
         * 转化编号
         */
        private String transformCode;

        /**
         * 取样编号，也是种植编号
         */
        private String sampleCode;

        /**
         * 是否检测
         */
        private String testFlag;

        /**
         * 检测结果
         */
        private String testResult;

        /**
         * 突变类型合计
         */
        private String mutationTypeSummary;

        /**
         * 是否审核
         */
        private String checkFlag;

        /**
         * 审核结果
         */
        private String checkResult;

        /**
         * 是否收获
         */
        private String harvestFlag;

        /**
         * 种子库种子编号
         */
        private String seedNum;

        /**
         * 当前种子代次
         */
        private String generation;

        /**
         * 种子总数量
         */
        private BigDecimal seedNumber;

        /**
         * 出库数量
         */
        private BigDecimal stockOutNumber;

        /**
         * 计量单位
         */
        private String unit;

        /**
         * 大田是否种植
         */
        private String fieldPlantFlag;

        /**
         * 大田收获种子编号
         */
        private String fieldHarvestSeedNum;

        /**
         * 大田收获种子数量
         */
        private String fieldHarvestSeedNumber;

        /**
         * 大田收获代次
         */
        private String fieldHarvestGeneration;
    }
}
