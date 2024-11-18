package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName cer_vector_group_tb
 */
@TableName(value ="cer_vector_group_tb")
@Data
public class CerVectorGroupTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 转化名称
     */
    private Integer vectorTaskId;

    /**
     * 质粒组（共转质粒）
     */
    private String plasmidNames;

    /**
     * 质检结果  pass已通过 refuse未通过
     */
    private String qualityInspectionResult;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 项目ID
     */
    private Integer projectId;

    private Integer repeatNum;

    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}