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
 * @Date：2023-08-28
 * @Description：角色表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value ="system_role_tb")
public class SystemRoleTb  implements Serializable {
    /**
     * 角色ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色状态 Y启用 N禁用
     */
    private String roleStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}