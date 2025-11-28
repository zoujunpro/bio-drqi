package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bio.drqi.common.enums.PlantStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * cer种植表
 *
 * @TableName plant_single_stock_tb
 */
@TableName(value = "plant_single_stock_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlantSingleStockTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 代次
     */
    private String generation;

    /**
     * 株树
     */
    private Integer plantNumber;

    /**
     * 播种日期
     */
    private String plantDate;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 移栽日期
     */
    private String transplantDate;

    /**
     * 春化开始日期
     */
    private String vernalizationBeginDate;

    /**
     * 春化结束日期
     */
    private String vernalizationEndDate;

    /**
     * 授粉方式
     */
    private String pollinationMethod;

    /**
     * 植株状态 1正常，2异常, 3已剔除，4已收获
     */
    private String plantStatus;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 收获日期
     */
    private String harvestDate;

    /**
     * 收获方式
     */
    private String harvestType;

    /**
     * 其他字段
     */
    private Object otherField;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 来源渠道 1项目，4种子库
     */
    private String sourceCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



    @TableField(exist = false)
    private Integer countNum;


    public static PlantSingleStockTb of(BioSampleTestTb bioSampleTestTb, PlantStatusEnum plantStatusEnum,String plantDate,String taskNum,String sourceCode,String remark) {
        PlantSingleStockTb plantSingleStockTb = new PlantSingleStockTb();
        plantSingleStockTb.setPlantCode(bioSampleTestTb.getSampleCode());
        plantSingleStockTb.setGeneration(bioSampleTestTb.getGeneration());
        plantSingleStockTb.setVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
        plantSingleStockTb.setSampleCode(bioSampleTestTb.getSampleCode());
        plantSingleStockTb.setSpeciesCode(bioSampleTestTb.getSpeciesCode());
        plantSingleStockTb.setCreateDate(new Date());
        plantSingleStockTb.setUpdateTime(new Date());
        plantSingleStockTb.setCreateUserId(bioSampleTestTb.getApplyUserId());
        plantSingleStockTb.setCreateUserName(bioSampleTestTb.getApplyUserName());
        plantSingleStockTb.setTaskNum(taskNum);
        plantSingleStockTb.setBreedCode(bioSampleTestTb.getBreedCode());
        plantSingleStockTb.setSourceCode(sourceCode);
        plantSingleStockTb.setPlantDate(plantDate);
        plantSingleStockTb.setRemark(remark);

        plantSingleStockTb.setPlantNumber(null);
        plantSingleStockTb.setTransplantDate(null);
        plantSingleStockTb.setVernalizationBeginDate(null);
        plantSingleStockTb.setVernalizationEndDate(null);
        plantSingleStockTb.setPollinationMethod(null);
        plantSingleStockTb.setPlantStatus(plantStatusEnum.code);
        plantSingleStockTb.setPollinationDate(null);
        plantSingleStockTb.setHarvestDate(null);
        plantSingleStockTb.setHarvestType(null);
        plantSingleStockTb.setOtherField(null);
        plantSingleStockTb.setEditType(null);

        return plantSingleStockTb;

    }


}