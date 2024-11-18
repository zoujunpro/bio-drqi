package com.bio.cer.seed;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SeedTestingListPageRspDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 种子号
     */
    private String seedNum;

    /**
     * 粒种
     */
    private String gw;

    /**
     * 性状纯度
     */
    private String tpur;

    /**
     * 品种纯度
     */
    private String gpur;

    /**
     * 转基因污染
     */
    private String gmi;

    /**
     * 净度
     */
    private String vcla;

    /**
     * 芽率
     */
    private String sgr;

    /**
     * 发芽势
     */
    private String gene;

    /**
     * 申请人用户人ID
     */
    private Integer applyUserId;

    /**
     * 申请人用户名
     */
    private String applyUserName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 任务编码
     */
    private String taskNum;
}
