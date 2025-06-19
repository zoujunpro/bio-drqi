package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 授粉无取样编号的单珠编号区间
 * @TableName tc_pollination_single_num_tb
 */
@TableName(value ="tc_pollination_single_num_tb")
@Data
public class TcPollinationSingleNumTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 试验编号
     */
    private String experimentNum;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 单株编号
     */
    private String singleNumber;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUserName;

    private String tcSingleNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}