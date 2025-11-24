package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bio.drqi.common.enums.SourceCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName plant_multiple_stock_tb
 */
@TableName(value = "plant_multiple_stock_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlantMultipleStockTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 代次
     */
    private String generation;

    /**
     * 数量
     */
    private Integer plantNumber;

    /**
     * 来源
     */
    private String sourceCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 工单编号(试验编号或者移苗编号)
     */
    private String taskNum;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 取样数量
     */
    private Integer sampleNumber;

    /**
     * 剩余数量
     */
    private Integer currentNumber;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * PD编号
     */
    private String pdNum;


    @TableField(exist = false)
    private String stockNumberNotNullFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public static PlantMultipleStockTb of(PlantExperimentDetailTb plantExperimentDetailTb, BioTaskDtlTb bioTaskDtlTb, SourceCodeEnum sourceCodeEnum) {
        PlantMultipleStockTb plantMultipleStockTb = new PlantMultipleStockTb();
        plantMultipleStockTb.setSeedNum(plantExperimentDetailTb.getSeedNum());
        plantMultipleStockTb.setTransformCode(null);
        plantMultipleStockTb.setGeneration(plantExperimentDetailTb.getGenerationCode());
        plantMultipleStockTb.setPlantNumber(plantExperimentDetailTb.getPlantNumber());
        plantMultipleStockTb.setSourceCode(sourceCodeEnum.name());
        plantMultipleStockTb.setRemark(plantExperimentDetailTb.getRemarks());
        plantMultipleStockTb.setCreateTime(bioTaskDtlTb.getCreateTime());
        plantMultipleStockTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
        plantMultipleStockTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
        plantMultipleStockTb.setTaskNum(bioTaskDtlTb.getTaskNum());
        plantMultipleStockTb.setSpeciesCode(plantExperimentDetailTb.getSpeciesCode());
        plantMultipleStockTb.setBreedCode(plantExperimentDetailTb.getBreedCode());
        plantMultipleStockTb.setSampleNumber(0);
        plantMultipleStockTb.setCurrentNumber(plantExperimentDetailTb.getPlantNumber());
        plantMultipleStockTb.setRegionNum(plantExperimentDetailTb.getRegionNum());
        plantMultipleStockTb.setVectorTaskCode(plantExperimentDetailTb.getVectorTaskCode());
        plantMultipleStockTb.setPdNum(plantExperimentDetailTb.getPdNum());
        return plantMultipleStockTb;
    }

}