package com.bio.cer.seed;

import com.bio.cer.BigDecimalSerialize;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeedStockInRspDTO {
    private Integer id;

    /**
     * 上级种子编号
     */
    private String seedNum;

    /**
     * 备注
     */
    private String remarks;

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
     * 种子来源（CER/温室/大田/外单位）
     */
    private String sourceType;

    /**
     * 申请任务编号
     */
    private String taskNum;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人姓名
     */
    private String applyUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
