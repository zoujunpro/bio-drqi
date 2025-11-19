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
 * @TableName plant_sample_coode_prefix_tb
 */
@TableName(value ="plant_sample_coode_prefix_tb")
@Data
public class PlantSampleCoodePrefixTb implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 取样编号前缀
     */
    private String sampleCodePrefix;

    /**
     * 当前索引
     */
    private Integer currentIndex;

    /**
     * 创建日期
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public PlantSampleCoodePrefixTb(String sampleCodePrefix) {
        this.sampleCodePrefix = sampleCodePrefix;
        this.createTime=new Date();
        this.currentIndex=0;
    }
}