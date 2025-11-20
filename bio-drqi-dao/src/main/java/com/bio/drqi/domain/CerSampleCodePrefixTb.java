package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 取样编号前缀表
 * @TableName cer_sample_code_prefix_tb
 */
@TableName(value ="cer_sample_code_prefix_tb")
@Data
public class CerSampleCodePrefixTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 取样编号前缀
     */
    private String sampleCodePrefix;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;


    private String plantExperimentCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}