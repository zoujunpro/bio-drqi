package com.bio.drqi.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * cer种植表
 * @TableName cer_plant_dtl_tb
 */
@Data
public class CerPlantDtlTb implements Serializable {
    /**
     * 主键ID
     */
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
     * 种子编号
     */
    private String plantCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 转化组合名称
     */
    private String transformGroupName;

    /**
     * 任务ID
     */
    private Integer vectorTaskId;

    /**
     * 任务编码
     */
    private String vectorTaskCode;

    /**
     * 质粒信息
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
     * 植株状态 1正常，异常
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
    private String speciesCode;

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateTime;


    private Integer createUserId;

    private String createUserName;




    private static final long serialVersionUID = 1L;


    public static CerPlantDtlTb of(CerSampleTestTb cerSampleTestTb,Integer createUserId,String createUserName){
        CerPlantDtlTb cerPlantDtlTb=new CerPlantDtlTb();
        cerPlantDtlTb.setProjectId(cerSampleTestTb.getProjectId());
        cerPlantDtlTb.setProjectCode(cerSampleTestTb.getProjectCode());
        cerPlantDtlTb.setSubProjectCode(cerSampleTestTb.getSubProjectCode());
        cerPlantDtlTb.setSubProjectId(cerSampleTestTb.getSubProjectId());
        cerPlantDtlTb.setVectorTaskId(cerSampleTestTb.getVectorTaskId());
        cerPlantDtlTb.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
        cerPlantDtlTb.setPlasmidName(cerSampleTestTb.getPlasmidName());
        cerPlantDtlTb.setTransformCode(cerSampleTestTb.getTransformCode());
        cerPlantDtlTb.setSampleCode(cerSampleTestTb.getSampleCode());
        cerPlantDtlTb.setGeneration(cerSampleTestTb.getSampleGeneration());
        cerPlantDtlTb.setPlantNumber(null);
        cerPlantDtlTb.setPlantDate(null);
        cerPlantDtlTb.setTransplantDate(null);
        cerPlantDtlTb.setVernalizationBeginDate(null);
        cerPlantDtlTb.setVernalizationEndDate(null);
        cerPlantDtlTb.setPollinationMethod(null);
        cerPlantDtlTb.setPlantStatus(null);
        cerPlantDtlTb.setFatherInfo(null);
        cerPlantDtlTb.setMotherInfo(null);
        cerPlantDtlTb.setPollinationDate(null);
        cerPlantDtlTb.setHarvestDate(null);
        cerPlantDtlTb.setOtherField(null);
        cerPlantDtlTb.setEditType(cerSampleTestTb.getTestEditType());
        cerPlantDtlTb.setSpeciesCode(cerSampleTestTb.getSampleCode());
        cerPlantDtlTb.setAcceptorMaterial(cerSampleTestTb.getAcceptorMaterial());
        cerPlantDtlTb.setCreateDate(new Date());
        cerPlantDtlTb.setUpdateTime(null);
        cerPlantDtlTb.setCreateUserId(createUserId);
        cerPlantDtlTb.setCreateUserName(createUserName);
        return cerPlantDtlTb;
    }

}