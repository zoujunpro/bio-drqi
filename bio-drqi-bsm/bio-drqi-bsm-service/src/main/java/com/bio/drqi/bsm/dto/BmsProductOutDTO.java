package com.bio.drqi.bsm.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BmsProductOutDTO {


    @NotBlank(message = "参数缺失：库存编号")
    private String uniqueCode;

    @NotBlank(message = "参数缺失：商品名称")
    private String productName;

    @NotBlank(message = "参数缺失：批次")
    private String batchNo;

    @NotBlank(message = "参数缺失：单位")
    private String unitCode;

    @NotBlank(message = "参数缺失：品牌")
    private String brandCode;

    @NotBlank(message = "参数缺失：规格")
    private String productSpecs;

    @NotNull(message = "参数缺失：出库数量")
    private Integer number;

    /**
     * 备注
     */
    private String remark;



}
