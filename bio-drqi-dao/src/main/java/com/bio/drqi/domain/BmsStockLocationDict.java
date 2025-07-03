package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 库位字典表
 * @TableName bms_stock_location_dict
 */
@TableName(value ="bms_stock_location_dict")
@Data
public class BmsStockLocationDict implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 单位编号
     */
    private String unitCode;

    /**
     * 库房编号
     */
    private String stockCode;

    /**
     * 库房名称
     */
    private String stockName;

    /**
     * 库位号
     */
    private String locationNumber;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创还时间
     */
    private Date createTime;

    private Integer kdNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}