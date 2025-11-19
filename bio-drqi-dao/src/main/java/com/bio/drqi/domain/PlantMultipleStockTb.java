package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bio.drqi.common.enums.SourceCodeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName plant_multiple_stock_tb
 */
@TableName(value = "plant_multiple_stock_tb")
@Data
public class PlantMultipleStockTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 移苗编号
     */
    private String transformCode;

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
     * 工单编号
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

    private String vectorTaskCode;

    private String pdNum;

    private String regionNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



    public static PlantMultipleStockTb of(PlantExperimentDetailTb plantExperimentDetailTb) {
        PlantMultipleStockTb plantMultipleStockTb = new PlantMultipleStockTb();
        plantMultipleStockTb.setSeedNum(plantExperimentDetailTb.getSeedNum());
        plantMultipleStockTb.setTransformCode(null);
        plantMultipleStockTb.setPlantNumber(plantExperimentDetailTb.getPlantNumber());
        plantMultipleStockTb.setSourceCode(SourceCodeEnum.project.name());
        plantMultipleStockTb.setRemark(plantExperimentDetailTb.getRemarks());
        plantMultipleStockTb.setCreateTime(plantExperimentDetailTb.getCreateTime());
        plantMultipleStockTb.setCreateUserId(plantExperimentDetailTb.getCreateUserId());
        plantMultipleStockTb.setCreateUserName(plantExperimentDetailTb.getCreateUserName());
        plantMultipleStockTb.setTaskNum(plantExperimentDetailTb.getExperimentNum());
        plantMultipleStockTb.setSpeciesCode(plantExperimentDetailTb.getSpeciesCode());
        plantMultipleStockTb.setBreedCode(plantExperimentDetailTb.getBreedCode());
        plantMultipleStockTb.setVectorTaskCode(plantExperimentDetailTb.getVectorTaskCode());
        plantMultipleStockTb.setPdNum(plantExperimentDetailTb.getPdNum());
        plantMultipleStockTb.setRegionNum(plantExperimentDetailTb.getRegionNum());
        plantMultipleStockTb.setSampleNumber(0);
        plantMultipleStockTb.setCurrentNumber(0);
        return plantMultipleStockTb;
    }
}