package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 田测授粉申请表
 * @TableName tc_pollination_apply_tb
 */
@TableName(value ="tc_pollination_apply_tb")
@Data
public class TcPollinationApplyTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 取样批次号
     */
    private String sampleApplyNum;

    /**
     * 授粉方式
     */
    private String pollinationType;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 任务工单号
     */
    private String taskNum;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 创建日期
     */
    private Date createTime;

    private String pollinationExcelUrl;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}