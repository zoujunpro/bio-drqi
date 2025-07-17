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
 * 
 * @TableName seed_quality_check_dtl_tb
 */
@TableName(value ="seed_quality_check_dtl_tb")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeedQualityCheckDtlTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 检测结果
     */
    private String checkResult;

    /**
     * 检测时间
     */
    private Date createTime;

    /**
     * 检测用户
     */
    private String createUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}