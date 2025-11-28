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
 * 转化表
 * @TableName cer_transform_tb
 */
@TableName(value ="cer_transform_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CerTransformTb implements Serializable {
    /**
     * 主键iD
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 载体任务ID
     */
    private Integer vectorTaskId;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 侵染数量
     */
    private Integer infectNumber;

    /**
     * 侵染日期
     */
    private String infectDate;

    /**
     * 递送方式（实际使用的方式）
     */
    private String deliveryMethod;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 载体任务编号
     */
    private String vectorTaskCode;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 任务状态 1审批中 2审批通过，3审批拒绝
     */
    private String taskStatus;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 质粒名称
     */
    private String plasmidName;

    private String speciesCode;

    private String breedCode;
    /**
     * 载体构建任务类型 1测试任务（普通），2测试任务（原生质体），3正常任务创建
     */
    private String vectorTaskType;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}