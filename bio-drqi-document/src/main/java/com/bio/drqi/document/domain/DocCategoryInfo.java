package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档分类信息表
 * @TableName doc_category_info
 */
@TableName(value ="doc_category_info")
@Data
public class DocCategoryInfo implements Serializable {
    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类编号，用于生成文档编号，如 TS
     */
    private String categoryCode;

    /**
     * 分类类型：PERSONAL/ENTERPRISE/KNOWLEDGE
     */
    private String categoryType;

    /**
     * 排序
     */
    private Integer sortNum;

    /**
     * 状态：1正常 0停用
     */
    private Integer status;

    /**
     * 目录负责人用户ID
     */
    private Long managerUserId;

    /**
     * 负责人管理范围：CURRENT当前目录 TREE当前目录及子目录
     */
    private String managerScope;

    /**
     * 是否继承上级目录权限：Y是 N否
     */
    private String inheritPermission;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除：0否 1是
     */
    private String deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
