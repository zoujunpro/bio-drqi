package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档信息表
 * @TableName doc_file_info
 */
@TableName(value ="doc_file_info")
@Data
public class DocFileInfo implements Serializable {
    /**
     * 文档ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文档名称
     */
    private String docName;

    /**
     * 文档编号
     */
    private String docCode;

    /**
     * 文件类型：docx/xlsx/pdf/pptx/png等
     */
    private String fileType;

    /**
     * 当前文件大小
     */
    private Long fileSize;

    /**
     * 当前版本ID
     */
    private Long currentVersionId;

    /**
     * 文档负责人/所有者
     */
    private Long ownerUserId;

    /**
     * 空间类型：PERSONAL/ENTERPRISE/KNOWLEDGE
     */
    private String spaceType;

    /**
     * 权限模式：INHERIT继承分类 CUSTOM自定义
     */
    private String permissionMode;

    /**
     * 有效期类型：PERMANENT永久 LIMITED指定有效期
     */
    private String effectiveType;

    /**
     * 生效时间
     */
    private Date effectiveStartTime;

    /**
     * 失效时间
     */
    private Date effectiveEndTime;

    /**
     * 状态：DRAFT/PENDING/PUBLISHED/OBSOLETE
     */
    private String status;

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

    /**
     * 备注
     */
    private String remark;

    /**
     * 当前登录用户是否已收藏
     */
    @TableField(exist = false)
    private Boolean isFavorite;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
