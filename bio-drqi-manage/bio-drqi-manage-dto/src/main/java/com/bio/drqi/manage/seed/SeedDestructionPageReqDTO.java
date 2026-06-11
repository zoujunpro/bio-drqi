package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SeedDestructionPageReqDTO extends PageDTO {
    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 销毁申请任务编号
     */
    private String taskNum;

    /**
     * 销毁申请任务编号，兼容前端 applyTaskNum 命名
     */
    private String applyTaskNum;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人姓名
     */
    private String applyUserName;

    /**
     * 销毁地点
     */
    private String destructionLocation;

    /**
     * 销毁方法
     */
    private String destructionMethod;

    /**
     * 种子来源
     */
    private String sourceType;

    /**
     * 种植编号
     */
    private String plantCode;

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
     * 物种编码
     */
    private String speciesCode;

    /**
     * 品种编码
     */
    private String breedCode;

    /**
     * 授粉方式
     */
    private String pollinationMethod;

    /**
     * 收获方式
     */
    private String harvestType;

    /**
     * 收获时间
     */
    private String harvestTime;

    /**
     * 生产地点编码
     */
    private String productionLocationCode;

    /**
     * 库位编号
     */
    private String stockLocationNum;

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
     * 材料类型
     */
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

    /**
     * 试验方案编号
     */
    private String experimentNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 父本单株编号
     */
    private String fatherSingleNum;

    /**
     * 母本单株编号
     */
    private String matherSingleNum;

    /**
     * PD号
     */
    private String pdImplementCode;

    /**
     * 销毁开始时间，格式：yyyyMMdd
     */
    private String beginDate;

    /**
     * 销毁结束时间，格式：yyyyMMdd
     */
    private String endDate;
}
