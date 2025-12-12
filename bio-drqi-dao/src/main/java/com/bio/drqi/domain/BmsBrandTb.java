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
 * 品牌信息表
 * @TableName bms_brand_tb
 */
@TableName(value ="bms_brand_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsBrandTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人iD
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    private String brandStatus;

    private String kdNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}