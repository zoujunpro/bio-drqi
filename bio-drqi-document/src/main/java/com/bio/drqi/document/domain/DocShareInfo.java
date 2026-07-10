package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档分享信息表
 * @TableName doc_share_info
 */
@TableName(value ="doc_share_info")
@Data
public class DocShareInfo implements Serializable {
    /**
     * 分享ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    private Long fileId;

    /**
     * 分享人ID
     */
    private Long shareUserId;

    /**
     * 分享类型：TARGET指定对象 LINK链接
     */
    private String shareType;

    /**
     * 分享链接Token
     */
    private String shareToken;

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
     * 过期时间
     */
    private Date expireTime;

    /**
     * 访问次数
     */
    private Integer accessCount;

    /**
     * 状态：ACTIVE有效 EXPIRED过期 DISABLED禁用
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}