package com.bio.drqi.bsm.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BmsProductUnsyncedExcelDTO {

    @ExcelProperty("商品名称")
    private String productName;

    @ExcelProperty("商品外部编号")
    private String productOutCode;

    @ExcelProperty("商品内部编号")
    private String productInnerCode;

    @ExcelIgnore
    private String productCategoryCode;

    @ExcelProperty("商品类别")
    private String productCategoryName;

    @ExcelIgnore
    private String brandCode;

    @ExcelProperty("品牌")
    private String brandName;

    @ExcelProperty("商品规格")
    private String productSpecs;

    @ExcelProperty("创建时间")
    private Date createTime;

    @ExcelProperty("创建人")
    private String createUserName;

    @ExcelProperty("付款类型")
    private String purchaseTypeCode;

    @ExcelProperty("金蝶同步编号")
    private String kdNumber;
}
