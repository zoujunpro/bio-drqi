package com.bio.drqi.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 取样编号前缀表
 * @TableName cer_sample_code_prefix_tb
 */
@Data
public class CerSampleCodePrefixTb implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 取样编号前缀
     */
    private String sampleCodePrefix;

    private String vectorTaskCode;

    /**
     * 创建日期
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;


}