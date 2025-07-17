package com.bio.drqi.bsm.rsp;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class BmsStockQueryByUnitRspDTO {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 库房名称
     */
    private String stockName;

    /**
     * 库房编号
     */
    private String stockCode;

    /**
     * 库房编号
     */
    private String unitCode;

    /**
     * 金蝶编号
     */
    private Integer kdNumber;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;
}
