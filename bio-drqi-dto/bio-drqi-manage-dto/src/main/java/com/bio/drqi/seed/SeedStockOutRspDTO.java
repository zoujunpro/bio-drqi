package com.bio.drqi.seed;

import com.bio.drqi.BigDecimalSerialize;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeedStockOutRspDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 种子号
     */
    private String seedNum;

    /**
     * 用途code
     */
    private String useToCode;

    /**
     * 计量单位g/kg/粒
     */
    private String unit;

    /**
     * 种子数量
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal seedNumber;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 任务号
     */
    private String taskNum;

    /**
     * 出库号
     */
    private String outTaskNum;

    /**
     * 用途描述
     */
    private String useToDesc;

}
