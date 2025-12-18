package com.bio.drqi.applet.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ScanCodeSeedRspDTO {

    private String projectCode;

    private String projectName;

    private String subProjectCode;

    private String vectorTaskCode;

    private String plasmidNames;

    private String transformCode;

    private List<Seed> seedList=new ArrayList<>();

    @Data
    public static class Seed {

        private Integer id;

        /**
         * 种植编号
         */
        private String plantCode;

        /**
         * 种子编号
         */
        private String seedNum;

        /**
         * 上一代种子编号
         */
        private String parentNum;

        /**
         * 父本信息
         */
        private String fatherInfo;

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
        private String speciesCode;

        /**
         * 项目物种
         */
        private String speciesName;

        /**
         * 品种
         */
        private String breedCode;

        private String breedName;

        /**
         * 授粉方式
         */
        private String pollinationMethod;

        private String pollinationMethodName;

        /**
         * 收获方式
         */
        private String harvestType;

        private String harvestTypeName;
        /**
         * 收获时间
         */
        private String harvestTime;

        /**
         * 种子数量
         */
        private BigDecimal seedNumber;

        /**
         * 计量单位g/kg/粒ml
         */
        private String unit;

        /**
         * 种子来源（1 CER/ 2 温室/3 大田/4 外单位）
         */
        private String sourceType;

        private String sourceTypeName;

        /**
         * 生产地点（天津/海南/新乡）
         */
        private String productionLocationCode;
        private String productionLocationName;
        /**
         * 库位编号
         */
        private String stockLocationNum;

        /**
         * 提交人ID
         */
        private Integer submitUserId;

        /**
         * 提交人姓名
         */
        private String submitUserName;

        /**
         * 创建日期
         */
        private Date createTime;

        /**
         * 更新日期
         */
        private Date updateTime;

        /**
         * 备注
         */
        private String remarks;

        /**
         * 入库时数量
         */
        private BigDecimal totalNumber;

        /**
         * 目标性状
         */
        private String targetCharacter;

        /**
         * 别名
         */
        private String aliasName;

        /**
         * 基因型
         */
        private String geneType;

        /**
         * 检测结果
         */
        private String checkResult;


        /**
         * 材料类型
         */
        private String materialType;

        private String materialTypeName;

        /**
         * 母本种子编号
         */
        private String matherSeedNum;

        /**
         * 父本种子编号
         */
        private String fatherSeedNum;

        /**
         * 母本小区编号
         */
        private String matherRegionNum;

        /**
         * 父本小区编号
         */
        private String fatherRegionNum;


        /**
         * 系谱
         */
        private String genealogy;

        /**
         * 是否基因分离
         */
        private String geneSeparateFlag;

        /**
         * 是否转基因
         */
        private String transFlag;

        /**
         * 实施方案编号
         */
        private String vectorTaskCode;


        private String experimentNum;

        private String projectCode;

        /**
         * 父本单株编号
         */
        private String fatherSingleNum;

        /**
         * 母本单株编号
         */
        private String matherSingleNum;

        private String pdImplementCode;

    }

}
