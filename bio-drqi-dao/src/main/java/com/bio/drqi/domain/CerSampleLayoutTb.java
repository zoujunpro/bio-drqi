package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName cer_sample_layout_tb
 */
@TableName(value ="cer_sample_layout_tb")
@Data
public class CerSampleLayoutTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 取样申请编号
     */
    private String applyNo;

    /**
     * 单管集合
     */
    private String singleContent;

    /**
     * 板集合
     */
    private String plateContent;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}