package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档权限信息表
 * @TableName doc_permission_info
 */
@TableName(value ="doc_permission_info")
@Data
public class DocPermissionInfo implements Serializable {
    /**
     * 权限ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源类型：CATEGORY/FILE
     */
    private String resourceType;

    /**
     * 资源ID：分类ID或文档ID
     */
    private Long resourceId;

    /**
     * 授权对象类型：USER/ROLE/DEPT
     */
    private String targetType;

    /**
     * 授权对象ID
     */
    private Long targetId;

    /**
     * 可查看
     */
    private Integer canView;

    /**
     * 可下载
     */
    private Integer canDownload;

    /**
     * 可编辑
     */
    private Integer canEdit;

    /**
     * 可分享
     */
    private Integer canShare;

    /**
     * 可删除
     */
    private Integer canDelete;

    /**
     * 可管理版本
     */
    private Integer canVersion;

    /**
     * 可管理权限
     */
    private Integer canPermission;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}