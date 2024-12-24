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
 * @TableName cer_sample_test_bio_info_result_tb
 */
@TableName(value ="cer_sample_test_bio_info_result_tb")
@Data
public class CerSampleTestBioInfoResultTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 材料名称
     */
    private String sampleId;

    /**
     * 生信系统唯一编号
     */
    private String uniqueDbCode;

    /**
     * 测序编号
     */
    private String runId;

    /**
     * HapID
     */
    private String hapId;

    /**
     * 变异类型合计
     */
    private String varType;

    /**
     * 突变方向合计
     */
    private String mutate;

    /**
     * 变异类型占比(%)
     */
    private String ratio;

    /**
     * 创建时间
     */
    private Date createTime;

    private String matchFlag;

    private String resultKey;

    private String confirmStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}