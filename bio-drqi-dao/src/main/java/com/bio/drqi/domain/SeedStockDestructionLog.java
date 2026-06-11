package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName seed_destruction_log
 */
@TableName(value ="seed_stock_destruction_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeedStockDestructionLog implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销毁地点
     */
    private String destructionLocation;

    /**
     * 种子号
     */
    private String seedNum;

    /**
     * 销毁方法
     */
    private String destructionMethod;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数量
     */
    private BigDecimal seedNumber;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 申请任务编号
     */
    private String taskNum;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 销毁证据
     */
    private String destructionEvidence;


    private Date destructionTime;

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
     * 项目物种
     */
    private String speciesCode;

    /**
     * 品种
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
     * 种子来源
     */
    private String sourceType;

    /**
     * 生产地点
     */
    private String productionLocationCode;

    /**
     * 库位编号
     */
    private String stockLocationNum;

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
     * 项目号
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
     * 销毁前库存数量
     */
    private BigDecimal stockBeforeNumber;

    /**
     * 销毁后库存数量
     */
    private BigDecimal stockAfterNumber;

    /**
     * yyyyMMdd 检索开始时间
     */
    @TableField(exist = false)
    private String beginDate;

    /**
     * yyyyMMdd 检索结束时间
     */
    @TableField(exist = false)
    private String endDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
