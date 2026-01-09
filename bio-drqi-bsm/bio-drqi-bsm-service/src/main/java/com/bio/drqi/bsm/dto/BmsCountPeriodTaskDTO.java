package com.bio.drqi.bsm.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class BmsCountPeriodTaskDTO {


    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 单位
     */
    private String unitCode;

    private String uniqueCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;
    /**
     * 库房编号
     */
    private String stockCode;

    /**
     * 当前库存
     */
    private Integer currentStockNumber;

}
