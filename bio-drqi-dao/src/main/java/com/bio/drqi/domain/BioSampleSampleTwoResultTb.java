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
 * @TableName bio_sample_sample_two_result_tb
 */
@TableName(value ="bio_sample_sample_two_result_tb")
@Data
public class BioSampleSampleTwoResultTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 材料名称
     */
    private String sampleId;

    /**
     * 测序编号
     */
    private String runId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上传编号
     */
    private String uploadNum;

    /**
     * 检测渠道 1 项目 2大田
     */
    private String testChannel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}