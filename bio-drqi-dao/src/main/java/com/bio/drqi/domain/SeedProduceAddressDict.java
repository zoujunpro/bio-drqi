package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 种子生产地点
 * @TableName seed_produce_address_dict
 */
@TableName(value ="seed_produce_address_dict")
@Data
public class SeedProduceAddressDict implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 地点名称
     */
    private String addressName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

    private String addressCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}