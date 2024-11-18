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
 * @TableName system_dept_tb
 */
@TableName(value ="system_dept_tb")
@Data
public class SystemDeptTb implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门父ID
     */
    private Integer parentId;

    /**
     * 排序id
     */
    private String orderNun;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 状态 Y启用 N禁用
     */
    private String status;

    /**
     * 级别
     */
    private Integer deptLevel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}