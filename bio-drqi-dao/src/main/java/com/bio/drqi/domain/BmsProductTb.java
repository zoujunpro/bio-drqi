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
 * 商品信息表
 * @TableName bms_product_tb
 */
@TableName(value ="bms_product_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsProductTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 创建日期
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

    /**
     * 删除标识
     */
    private String productStatus;
    /**
     * 图片
     */
    private String pictureUrls;

    private String kdNumber;

    private Date updateTime;

    @TableField(exist = false)
    private String startDate;

    @TableField(exist = false)
    private String endDate;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}