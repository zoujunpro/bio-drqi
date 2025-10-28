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
 * @TableName cer_sample_test_bio_result_ref
 */
@TableName(value ="cer_sample_test_bio_result_ref")
@Data
public class CerSampleTestBioResultRef implements Serializable {
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
     * 实施方案编号
     */
    private String sampleCode;

    /**
     * 取样编号
     */
    private String vectorTaskCode;

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


    private String uploadNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}