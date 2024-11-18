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

/**
 * @Date：2023-09-20
 * @Description：系统信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value ="system_sys_tb")
public class SystemSysTb  implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 状态 Y启用 N禁用
     */
    private String status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}