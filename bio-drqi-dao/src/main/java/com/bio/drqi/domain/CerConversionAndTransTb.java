package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 转化疫苗记录表
 * @TableName cer_conversion_and_trans_tb
 */
@TableName(value ="cer_conversion_and_trans_tb")
@Data
public class CerConversionAndTransTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 交接日期
     */
    private String handoverDate;



    /**
     * 提交日期
     */
    private Date createTime;

    /**
     * 提交人
     */
    private Integer createUserId;

    /**
     * 提交人名称
     */
    private String createUserName;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 交接数量
     */
    private Integer transNumber;

    private String remark;

    private String transType;


    @TableField(exist = false)
    private String transDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}