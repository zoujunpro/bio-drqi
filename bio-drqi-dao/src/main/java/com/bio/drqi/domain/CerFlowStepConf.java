package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Date：2023-08-31
 * @Description：
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="cer_flow_step_conf")
public class CerFlowStepConf {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 步骤编码
     */
    private String flowStepCode;

    /**
     * 步骤名称
     */
    private String flowStepName;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;


    private String flowStepType;

    private Integer parentId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}