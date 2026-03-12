package com.bio.drqi.bsm.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsProductStockInLogListPageRspDTO {
    /**
     * 主键ID
     */
    @ExcelIgnore
    private Integer id;
    /**
     * 订单编号
     */
    @ExcelProperty("采购订单编号")
    private String orderNum;
    /**
     * 子订单编号
     */
    @ExcelProperty("订单明细编号")
    private String orderDetailNum;

    /**
     * 商品名称
     */
    @ExcelProperty("商品名称")
    private String productName;

    /**
     * 商品内部编号
     */
    @ExcelProperty("商品内部编号")
    private String productInnerCode;

    /**
     * 商品外部编号
     */
    @ExcelProperty("商品外部编号")
    private String productOutCode;

    /**
     * 所属类别编号
     */
    @ExcelIgnore
    private String productCategoryCode;

    /**
     * 所属类别编号
     */
    @ExcelProperty("商品类别")
    private String productCategoryName;

    /**
     * 品牌编号
     */
    @ExcelIgnore
    private String brandCode;

    /**
     * 品牌名称
     */
    @ExcelProperty("品牌")
    private String brandName;

    /**
     * 商品规格
     */
    @ExcelProperty("商品规格")
    private String productSpecs;

    /**
     * 商品批次
     */
    @ExcelProperty("商品批次")
    private String batchNo;

    /**
     * 研发项目
     */
    @ExcelProperty("研发项目")
    private String projectCode;

    /**
     * 入库单价
     */
    @ExcelProperty("入库单价")
    private BigDecimal productPrice;

    /**
     * 入库数量
     */
    @ExcelProperty("入库数量")
    private BigDecimal storeNumber;

    /**
     * 入库金额
     */
    @ExcelProperty("入库金额")
    private BigDecimal storeAmount;

    /**
     * 申请人ID
     */
    @ExcelIgnore
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    @ExcelProperty("申请人")
    private String applyUserName;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private Date createTime;

    /**
     * 任务编号
     */
    @ExcelIgnore
    private String taskNum;



    /**
     * 库存位置编号
     */
    @ExcelIgnore
    private String stockLocationNumber;

    /**
     * 单位编号
     */
    @ExcelProperty("单位编号")
    private String unitCode;

    /**
     * 供应商名称
     */
    @ExcelProperty("供应商名称")
    private String supplierName;

    /**
     * 供应商编号
     */
    @ExcelProperty("供应商编号")
    private String supplierCode;

    /**
     * 唯一编号
     */
    @ExcelProperty("库存唯一编码")
    private String uniqueCode;


    @ExcelProperty("生产日期")
    private String produceDate;

    @ExcelProperty("有效期")
    private String expirationDate;

    @ExcelProperty("税率")
    private String taxRate;

    @ExcelProperty("退货数量")
    private BigDecimal returnNumber;

    @ExcelIgnore
    private String stockCode;

    @ExcelProperty("库房")
    private String stockName;

    @ExcelProperty("金蝶同步反馈结果")
    private Integer kdNumber;

    @ExcelIgnore
    private String payType;

    @ExcelProperty("付款类型")
    private String payTypeName;
}
