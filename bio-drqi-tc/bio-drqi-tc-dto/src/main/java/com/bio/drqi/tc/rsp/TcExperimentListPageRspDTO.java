package com.bio.drqi.tc.rsp;

import lombok.Data;

@Data
public class TcExperimentListPageRspDTO {
    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 物种
     */
    private String speciesName;

    /**
     * 品种
     */
    private String breedName;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 株系名称
     */
    private String strainName;

    /**
     * 目标性状
     */
    private String targetCharacter;

    /**
     * 代次编号
     */
    private String generationCode;

    /**
     * 田测基因型
     */
    private String tcGene;

    /**
     * 密度
     */
    private String density;

    /**
     * 组别
     */
    private String groupName;

    /**
     * 重复
     */
    private String repeatNum;

    /**
     * 小区面积
     */
    private String regionArea;

    /**
     * 面积单位
     */
    private String areaUnit;

    /**
     * 株距
     */
    private String plantSpace;

    /**
     * 行数
     */
    private String rowsNumber;

    /**
     * 行长
     */
    private String rowsLength;

    /**
     * 行距
     */
    private String rowsSpace;

    /**
     * 播种方式
     */
    private String seedingType;

    /**
     * 每穴播种粒数
     */
    private String perHoleSeedingNumber;

    /**
     * 每行播种数量
     */
    private String rowSeedingNumber;

    /**
     * 小区播种数量
     */
    private Integer seedingNumber;

    /**
     * 播种单位
     */
    private String seedingUnit;

    /**
     * 期次
     */
    private String period;

    /**
     * 亲本类型
     */
    private String parentType;

    /**
     * 错期设计
     */
    private String staggeredDesign;

    /**
     * 保留苗数量
     */
    private Integer stayNumber;

    /**
     * PD实施方案编号
     */
    private String pdImplementCode;

    private String pdNum;
}
