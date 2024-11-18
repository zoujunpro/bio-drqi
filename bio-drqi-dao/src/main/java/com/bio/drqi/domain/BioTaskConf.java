package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务配置信息
 * @TableName bio_task_conf
 */
@TableName(value ="bio_task_conf")
@Data
public class BioTaskConf implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 任务工单类型名称
     */
    private String taskTypeName;

    /**
     * 任务工单类型
     */
    private String taskTypeCode;

    /**
     * 流程ID
     */
    private Long processId;

    /**
     * 任务类别 project  seed
     */
    private String taskCategory;

    /**
     * 更新时间
     */
    private Date createTime;

    private String beginLetter;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}