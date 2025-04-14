package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 转化疫苗取样编号关联表
 * @TableName cer_conversion_and_trans_ref
 */
@TableName(value ="cer_conversion_and_trans_ref")
@Data
public class CerConversionAndTransRef implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 转化疫苗ID
     */
    private Integer conversionAndTransId;

    /**
     * 取样编号
     */
    private String sampleCode;


    private String editPureUnion;

    private String acceptorMaterial;

    private String vectorTaskCode;

    private String subProjectCode;

    private String transformCode;

    private Integer transNum;

    /**
     * 项目编号
     */
    private String projectCode;
    /**
     * 是否转基因 Y-是,N-否 O-N/A
     */
    private String transGeneFlag;

    private String plasmidName;

    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}