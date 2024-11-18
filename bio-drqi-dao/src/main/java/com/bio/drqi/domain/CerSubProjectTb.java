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
 * @TableName cer_sub_project_tb
 */
@TableName(value ="cer_sub_project_tb")
@Data
public class CerSubProjectTb implements Serializable {
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
     * 子项目名称
     */
    private String subProjectName;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 附件地址
     */
    private String fileUrls;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 1审批中 2审批通过，3审批拒绝
     */
    private String taskStatus;

    private String priorityLevel;

    private String speciesCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}