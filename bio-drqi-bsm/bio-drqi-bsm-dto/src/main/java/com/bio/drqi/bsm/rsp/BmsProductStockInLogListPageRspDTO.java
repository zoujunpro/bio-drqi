package com.bio.drqi.bsm.rsp;

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
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 子订单编号
     */
    private String orderDetailNum;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品内部编号
     */
    private String productInnerCode;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 所属类别编号
     */
    private String productCategoryCode;

    /**
     * 货品类型编号
     */
    private String productTypeCode;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 研发项目
     */
    private String projectCode;

    /**
     * 入库单价
     */
    private BigDecimal productPrice;

    /**
     * 入库数量
     */
    private Integer storeNumber;

    /**
     * 入库金额
     */
    private BigDecimal storeAmount;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 库存位置编号
     */
    private String stockLocationNumber;

    /**
     * 单位编号
     */
    private String unitCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 唯一编号
     */
    private String uniqueCode;


    private String produceDate;

    private String expirationDate;
}
