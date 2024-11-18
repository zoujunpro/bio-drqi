package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 物种配置信息表
 * @TableName cer_species_conf
 */
@TableName(value ="cer_species_conf")
@Data
public class CerSpeciesConf implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物种名称
     */
    private String speciesName;

    /**
     * 物种编码
     */
    private String speciesCode;

    private String numPrefix;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}