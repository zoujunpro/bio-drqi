package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 出库记录日志表
 * @TableName bms_product_stock_out_log
 */
@TableName(value ="bms_product_stock_out_log")
@Data
public class BmsProductStockOutLog implements Serializable {
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
     * 所属类别编号
     */
    private String productCategoryCode;

    /**
     * 货品类型编号
     */
    private String productTypeCode;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 出库数量
     */
    private Integer outNumber;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 出库备注
     */
    private String remark;

    /**
     * 出库类型 1正常出库 2退货出库
     */
    private String outType;

    /**
     * 单位编号
     */
    private String unitCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}