package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品类型ID
 * @TableName bms_product_type_tb
 */
@TableName(value ="bms_product_type_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsProductTypeTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品类型编号
     */
    private String productTypeCode;

    /**
     * 商品类型名称
     */
    private String productTypeName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}