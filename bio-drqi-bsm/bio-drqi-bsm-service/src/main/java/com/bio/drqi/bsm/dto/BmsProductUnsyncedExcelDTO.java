package com.bio.drqi.bsm.dto;

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

    @ExcelProperty("商品类别编号")
    private String productCategoryCode;

    @ExcelProperty("商品类别名称")
    private String productCategoryName;

    @ExcelProperty("品牌编号")
    private String brandCode;

    @ExcelProperty("品牌名称")
    private String brandName;

    @ExcelProperty("商品规格")
    private String productSpecs;

    @ExcelProperty("常规采购标记")
    private String purchaseTypeCode;

    @ExcelProperty("创建时间")
    private Date createTime;

    @ExcelProperty("创建人")
    private String createUserName;

    @ExcelProperty("更新时间")
    private Date updateTime;

    @ExcelProperty("商品状态")
    private String productStatus;

    @ExcelProperty("金蝶同步编号")
    private String kdNumber;
}
