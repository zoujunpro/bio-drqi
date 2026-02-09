package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.BigDecimalSerialize;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeedStockPageRspDTO {

    /**
     * 主键ID
     */
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
     * 项目号
     */
    private String projectCode;

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
    @JsonSerialize(using = BigDecimalSerialize.class)
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
     * 生产地点（天津/海南/新乡）
     */
    private String productionLocationCode;

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
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalNumber;

    private String checkResult;



    private String targetCharacter;

    private String aliasName;

    private String geneType;


    private String materialType;


    /**
     * 母本种子编号
     */
    private String matherSeedNum;

    /**
     * 父本种子编号
     */
    private String fatherSeedNum;
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

    /**
     * 父本单株编号
     */
    private String fatherSingleNum;

    /**
     * 母本单株编号
     */
    private String matherSingleNum;

    /**
     * 母本小区编号
     */
    private String matherRegionNum;

    /**
     * 父本小区编号
     */
    private String fatherRegionNum;

    private String pdImplementCode;

    private String spotCheckResult;



}
