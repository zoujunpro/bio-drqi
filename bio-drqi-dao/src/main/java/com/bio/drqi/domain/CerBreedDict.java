package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName cer_breed_dict
 */
@TableName(value ="cer_breed_dict")
@Data
public class CerBreedDict implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String breedCode;

    /**
     * 
     */
    private String breedName;

    /**
     * 物种
     */
    private String speciesCode;

    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}