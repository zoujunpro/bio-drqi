package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * cer种植表
 *
 * @TableName cer_plant_tb
 */
@TableName(value = "cer_plant_tb")
@Data
public class CerPlantTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属项目ID
     */
    private Integer projectId;

    /**
     * 所属项目编码
     */
    private String projectCode;

    /**
     * 种子编号/取样编号
     */
    private String cerNumber;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 任务ID
     */
    private Integer vectorTaskId;

    /**
     * 任务编码
     */
    private String vectorTaskCode;

    /**
     * 质粒名称
     */
    private String plasmidName;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 代次
     */
    private String generation;

    /**
     * 株树
     */
    private Integer plantNumber;

    /**
     * 播种/移苗日期
     */
    private String plantDate;

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
     * 植株状态
     */
    private String plantStatus;

    /**
     * 父本信息
     */
    private String fatherInfo;

    /**
     * 母本信息
     */
    private String motherInfo;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 收获日期
     */
    private String harvestDate;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 其他字段
     */
    private Object otherField;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 项目物种
     */
    private String species;

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 检测结果
     */
    private String checkResult;

    private String taskNum;

    private String taskStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public void removeNullStr() {

        if ("null".equals(cerNumber)) {
            this.cerNumber = null;
        }
        if ("null".equals(plantNumber)) {
            this.plantNumber = null;
        }
        if ("null".equals(plantDate)) {
            this.plantDate = null;
        }
        if ("null".equals(transplantDate)) {
            this.transplantDate = null;
        }
        if ("null".equals(vernalizationBeginDate)) {
            this.vernalizationBeginDate = null;
        }
        if ("null".equals(vernalizationEndDate)) {
            this.vernalizationEndDate = null;
        }
        if ("null".equals(pollinationMethod)) {
            this.pollinationMethod = null;
        }
        if ("null".equals(plantStatus)) {
            this.plantStatus = null;
        }
        if ("null".equals(fatherInfo)) {
            this.fatherInfo = null;
        }
        if ("null".equals(motherInfo)) {
            this.motherInfo = null;
        }
        if ("null".equals(pollinationDate)) {
            this.pollinationDate = null;
        }
        if ("null".equals(harvestDate)) {
            this.harvestDate = null;
        }
    }

}