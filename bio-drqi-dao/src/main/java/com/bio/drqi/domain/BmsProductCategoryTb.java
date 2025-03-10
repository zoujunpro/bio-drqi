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
 * 商品类别表
 * @TableName bms_product_category_tb
 */
@TableName(value ="bms_product_category_tb")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BmsProductCategoryTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 商品类型名称
     */
    private String productCategoryName;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;

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