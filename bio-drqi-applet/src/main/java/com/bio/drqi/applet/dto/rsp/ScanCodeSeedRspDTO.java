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

    private String subProjectName;

    private String vectorTaskCode;

    private String vectorTaskName;

    private String plasmidNames;

    private String transformCode;

    private List<Seed> seedList=new ArrayList<>();

    @Data
    public static class Seed {

        /**
         * 种植编号
         */
        private String plantNum;

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
        private String speciesCode;

        /**
         * 项目物种
         */
        private String speciesName;

        /**
         * 品种
         */
        private String breedCode;

        /**
         * 品种
         */
        private String breedName;

        /**
         * 授粉方式
         */
        private String pollinationMethod;

        /**
         * 种子类型  自交/杂交
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
        private BigDecimal seedNumber;

        /**
         * 计量单位g/kg/粒
         */
        private String unit;

        /**
         * 种子来源（1 CER/ 2 温室/3 大田/4 外单位）
         */
        private String sourceType;


        /**
         * 生产地点（天津/海南/新乡）
         */
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
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date createTime;

        /**
         * 更新日期
         */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date updateTime;

        /**
         * 备注
         */
        private String remarks;

        /**
         * 入库时库存
         */
        private BigDecimal totalNumber;

        /**
         * 基因型性状
         */
        private String geneticCharacter;

        /**
         * 别名
         */
        private String aliasName;

        /**
         * 基因类型
         */
        private String geneType;

        /**
         * 材料类型
         */
        private String materialType;

    }

}
