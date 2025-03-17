package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 入库记录日志表
 * @TableName bms_product_stock_in_log
 */
@TableName(value ="bms_product_stock_in_log")
public class BmsProductStockInLog implements Serializable {
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
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;

    /**
     * 商品类别名称
     */
    private String productCategoryName;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 子订单编号
     */
    public String getOrderDetailNum() {
        return orderDetailNum;
    }

    /**
     * 子订单编号
     */
    public void setOrderDetailNum(String orderDetailNum) {
        this.orderDetailNum = orderDetailNum;
    }

    /**
     * 商品名称
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 商品名称
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 商品外部编号
     */
    public String getProductOutCode() {
        return productOutCode;
    }

    /**
     * 商品外部编号
     */
    public void setProductOutCode(String productOutCode) {
        this.productOutCode = productOutCode;
    }

    /**
     * 商品类别编号
     */
    public String getProductCategoryCode() {
        return productCategoryCode;
    }

    /**
     * 商品类别编号
     */
    public void setProductCategoryCode(String productCategoryCode) {
        this.productCategoryCode = productCategoryCode;
    }

    /**
     * 商品类别名称
     */
    public String getProductCategoryName() {
        return productCategoryName;
    }

    /**
     * 商品类别名称
     */
    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    /**
     * 品牌编号
     */
    public String getBrandCode() {
        return brandCode;
    }

    /**
     * 品牌编号
     */
    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    /**
     * 品牌名称
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * 品牌名称
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * 商品规格
     */
    public String getProductSpecs() {
        return productSpecs;
    }

    /**
     * 商品规格
     */
    public void setProductSpecs(String productSpecs) {
        this.productSpecs = productSpecs;
    }

    /**
     * 商品批次
     */
    public String getBatchNo() {
        return batchNo;
    }

    /**
     * 商品批次
     */
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    /**
     * 研发项目
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 研发项目
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * 入库单价
     */
    public BigDecimal getProductPrice() {
        return productPrice;
    }

    /**
     * 入库单价
     */
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * 入库数量
     */
    public Integer getStoreNumber() {
        return storeNumber;
    }

    /**
     * 入库数量
     */
    public void setStoreNumber(Integer storeNumber) {
        this.storeNumber = storeNumber;
    }

    /**
     * 入库金额
     */
    public BigDecimal getStoreAmount() {
        return storeAmount;
    }

    /**
     * 入库金额
     */
    public void setStoreAmount(BigDecimal storeAmount) {
        this.storeAmount = storeAmount;
    }

    /**
     * 申请人ID
     */
    public Integer getApplyUserId() {
        return applyUserId;
    }

    /**
     * 申请人ID
     */
    public void setApplyUserId(Integer applyUserId) {
        this.applyUserId = applyUserId;
    }

    /**
     * 申请人名称
     */
    public String getApplyUserName() {
        return applyUserName;
    }

    /**
     * 申请人名称
     */
    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 任务编号
     */
    public String getTaskNum() {
        return taskNum;
    }

    /**
     * 任务编号
     */
    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    /**
     * 订单编号
     */
    public String getOrderNum() {
        return orderNum;
    }

    /**
     * 订单编号
     */
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 库存位置编号
     */
    public String getStockLocationNumber() {
        return stockLocationNumber;
    }

    /**
     * 库存位置编号
     */
    public void setStockLocationNumber(String stockLocationNumber) {
        this.stockLocationNumber = stockLocationNumber;
    }

    /**
     * 单位编号
     */
    public String getUnitCode() {
        return unitCode;
    }

    /**
     * 单位编号
     */
    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BmsProductStockInLog other = (BmsProductStockInLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderDetailNum() == null ? other.getOrderDetailNum() == null : this.getOrderDetailNum().equals(other.getOrderDetailNum()))
            && (this.getProductName() == null ? other.getProductName() == null : this.getProductName().equals(other.getProductName()))
            && (this.getProductOutCode() == null ? other.getProductOutCode() == null : this.getProductOutCode().equals(other.getProductOutCode()))
            && (this.getProductCategoryCode() == null ? other.getProductCategoryCode() == null : this.getProductCategoryCode().equals(other.getProductCategoryCode()))
            && (this.getProductCategoryName() == null ? other.getProductCategoryName() == null : this.getProductCategoryName().equals(other.getProductCategoryName()))
            && (this.getBrandCode() == null ? other.getBrandCode() == null : this.getBrandCode().equals(other.getBrandCode()))
            && (this.getBrandName() == null ? other.getBrandName() == null : this.getBrandName().equals(other.getBrandName()))
            && (this.getProductSpecs() == null ? other.getProductSpecs() == null : this.getProductSpecs().equals(other.getProductSpecs()))
            && (this.getBatchNo() == null ? other.getBatchNo() == null : this.getBatchNo().equals(other.getBatchNo()))
            && (this.getProjectCode() == null ? other.getProjectCode() == null : this.getProjectCode().equals(other.getProjectCode()))
            && (this.getProductPrice() == null ? other.getProductPrice() == null : this.getProductPrice().equals(other.getProductPrice()))
            && (this.getStoreNumber() == null ? other.getStoreNumber() == null : this.getStoreNumber().equals(other.getStoreNumber()))
            && (this.getStoreAmount() == null ? other.getStoreAmount() == null : this.getStoreAmount().equals(other.getStoreAmount()))
            && (this.getApplyUserId() == null ? other.getApplyUserId() == null : this.getApplyUserId().equals(other.getApplyUserId()))
            && (this.getApplyUserName() == null ? other.getApplyUserName() == null : this.getApplyUserName().equals(other.getApplyUserName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()))
            && (this.getOrderNum() == null ? other.getOrderNum() == null : this.getOrderNum().equals(other.getOrderNum()))
            && (this.getStockLocationNumber() == null ? other.getStockLocationNumber() == null : this.getStockLocationNumber().equals(other.getStockLocationNumber()))
            && (this.getUnitCode() == null ? other.getUnitCode() == null : this.getUnitCode().equals(other.getUnitCode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderDetailNum() == null) ? 0 : getOrderDetailNum().hashCode());
        result = prime * result + ((getProductName() == null) ? 0 : getProductName().hashCode());
        result = prime * result + ((getProductOutCode() == null) ? 0 : getProductOutCode().hashCode());
        result = prime * result + ((getProductCategoryCode() == null) ? 0 : getProductCategoryCode().hashCode());
        result = prime * result + ((getProductCategoryName() == null) ? 0 : getProductCategoryName().hashCode());
        result = prime * result + ((getBrandCode() == null) ? 0 : getBrandCode().hashCode());
        result = prime * result + ((getBrandName() == null) ? 0 : getBrandName().hashCode());
        result = prime * result + ((getProductSpecs() == null) ? 0 : getProductSpecs().hashCode());
        result = prime * result + ((getBatchNo() == null) ? 0 : getBatchNo().hashCode());
        result = prime * result + ((getProjectCode() == null) ? 0 : getProjectCode().hashCode());
        result = prime * result + ((getProductPrice() == null) ? 0 : getProductPrice().hashCode());
        result = prime * result + ((getStoreNumber() == null) ? 0 : getStoreNumber().hashCode());
        result = prime * result + ((getStoreAmount() == null) ? 0 : getStoreAmount().hashCode());
        result = prime * result + ((getApplyUserId() == null) ? 0 : getApplyUserId().hashCode());
        result = prime * result + ((getApplyUserName() == null) ? 0 : getApplyUserName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        result = prime * result + ((getOrderNum() == null) ? 0 : getOrderNum().hashCode());
        result = prime * result + ((getStockLocationNumber() == null) ? 0 : getStockLocationNumber().hashCode());
        result = prime * result + ((getUnitCode() == null) ? 0 : getUnitCode().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderDetailNum=").append(orderDetailNum);
        sb.append(", productName=").append(productName);
        sb.append(", productOutCode=").append(productOutCode);
        sb.append(", productCategoryCode=").append(productCategoryCode);
        sb.append(", productCategoryName=").append(productCategoryName);
        sb.append(", brandCode=").append(brandCode);
        sb.append(", brandName=").append(brandName);
        sb.append(", productSpecs=").append(productSpecs);
        sb.append(", batchNo=").append(batchNo);
        sb.append(", projectCode=").append(projectCode);
        sb.append(", productPrice=").append(productPrice);
        sb.append(", storeNumber=").append(storeNumber);
        sb.append(", storeAmount=").append(storeAmount);
        sb.append(", applyUserId=").append(applyUserId);
        sb.append(", applyUserName=").append(applyUserName);
        sb.append(", createTime=").append(createTime);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", orderNum=").append(orderNum);
        sb.append(", stockLocationNumber=").append(stockLocationNumber);
        sb.append(", unitCode=").append(unitCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}