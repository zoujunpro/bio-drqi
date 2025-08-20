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
 * @Date：2023-09-21
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "cer_project_tb")
public class CerProjectTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 项目状态 0未执行 1执行中  2暂停 3结束
     */
    private String projectStatus;


    private String projectType;
    /**
     * 项目创建时间
     */
    private Date createTime;

    /**
     * 项目更新时间
     */
    private Date updateTime;

    /**
     * 项目创建人
     */
    private Integer createUserId;

    /**
     * 项目负责人
     */
    private Integer ownerUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 负责人名称
     */
    private String ownerUserName;

    /**
     * 编辑类型  1基因编辑 2转基因
     */
    private String geneEditMethod;

    /**
     * 项目预计开始日期
     */
    private String expectStartDate;

    /**
     * 任务编号
     */
    private String taskNum;

    private String projectCategoryCode;


    @TableField(exist = false)
    private Integer countNum;

    @TableField(exist = false)
    private Integer actorId;

    @TableField(exist = false)
    private String orderType;

    @TableField(exist = false)
    private String orderField;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}