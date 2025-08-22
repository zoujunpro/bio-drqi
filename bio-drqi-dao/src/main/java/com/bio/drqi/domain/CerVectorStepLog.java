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
 * @TableName cer_vector_step_log
 */
@TableName(value ="cer_vector_step_log")
@Data
public class CerVectorStepLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 实施方案ID
     */
    private Integer vectorTaskId;

    /**
     * 步骤
     */
    private String stepCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 任务编号
     */
    private String taskNum;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}