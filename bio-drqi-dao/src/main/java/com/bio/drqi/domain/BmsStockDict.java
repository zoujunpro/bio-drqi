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
 * @TableName bms_stock_dict
 */
@TableName(value ="bms_stock_dict")
@Data
public class BmsStockDict implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 库房名称
     */
    private String stockName;

    /**
     * 库房编号
     */
    private String stockCode;

    /**
     * 库房编号
     */
    private String unitCode;

    /**
     * 金蝶编号
     */
    private Integer kdNumber;

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