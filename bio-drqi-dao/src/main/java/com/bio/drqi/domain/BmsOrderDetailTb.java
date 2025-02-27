package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单明细表
 * @TableName bms_order_detail_tb
 */
@TableName(value ="bms_order_detail_tb")
public class BmsOrderDetailTb implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 子订单编号
     */
    private String orderDetailNum;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品规格
     */
    private String productSku;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 采购单价
     */
    private BigDecimal purchasePrice;

    /**
     * 采购数量
     */
    private Integer purchaseNumber;

    /**
     * 付款金额
     */
    private BigDecimal payAmount;

    /**
     * 实际付款金额
     */
    private BigDecimal actualPayAmount;

    /**
     * 实际采购数量
     */
    private Integer actualPurchaseNumber;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;

    /**
     * 商品类别名称
     */
    private String productCategoryName;

    /**
     * 商品附件图
     */
    private String pictureUrls;

    /**
     * 质保时间
     */
    private String warrantyDesc;

    /**
     * 创建时间
     */
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
     * 任务编号
     */
    private String taskNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 商品规格
     */
    public String getProductSku() {
        return productSku;
    }

    /**
     * 商品规格
     */
    public void setProductSku(String productSku) {
        this.productSku = productSku;
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
     * 采购单价
     */
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * 采购单价
     */
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /**
     * 采购数量
     */
    public Integer getPurchaseNumber() {
        return purchaseNumber;
    }

    /**
     * 采购数量
     */
    public void setPurchaseNumber(Integer purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    /**
     * 付款金额
     */
    public BigDecimal getPayAmount() {
        return payAmount;
    }

    /**
     * 付款金额
     */
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    /**
     * 实际付款金额
     */
    public BigDecimal getActualPayAmount() {
        return actualPayAmount;
    }

    /**
     * 实际付款金额
     */
    public void setActualPayAmount(BigDecimal actualPayAmount) {
        this.actualPayAmount = actualPayAmount;
    }

    /**
     * 实际采购数量
     */
    public Integer getActualPurchaseNumber() {
        return actualPurchaseNumber;
    }

    /**
     * 实际采购数量
     */
    public void setActualPurchaseNumber(Integer actualPurchaseNumber) {
        this.actualPurchaseNumber = actualPurchaseNumber;
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
     * 商品附件图
     */
    public String getPictureUrls() {
        return pictureUrls;
    }

    /**
     * 商品附件图
     */
    public void setPictureUrls(String pictureUrls) {
        this.pictureUrls = pictureUrls;
    }

    /**
     * 质保时间
     */
    public String getWarrantyDesc() {
        return warrantyDesc;
    }

    /**
     * 质保时间
     */
    public void setWarrantyDesc(String warrantyDesc) {
        this.warrantyDesc = warrantyDesc;
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
        BmsOrderDetailTb other = (BmsOrderDetailTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNum() == null ? other.getOrderNum() == null : this.getOrderNum().equals(other.getOrderNum()))
            && (this.getOrderDetailNum() == null ? other.getOrderDetailNum() == null : this.getOrderDetailNum().equals(other.getOrderDetailNum()))
            && (this.getBrandCode() == null ? other.getBrandCode() == null : this.getBrandCode().equals(other.getBrandCode()))
            && (this.getBrandName() == null ? other.getBrandName() == null : this.getBrandName().equals(other.getBrandName()))
            && (this.getProductName() == null ? other.getProductName() == null : this.getProductName().equals(other.getProductName()))
            && (this.getProductSku() == null ? other.getProductSku() == null : this.getProductSku().equals(other.getProductSku()))
            && (this.getProductOutCode() == null ? other.getProductOutCode() == null : this.getProductOutCode().equals(other.getProductOutCode()))
            && (this.getPurchasePrice() == null ? other.getPurchasePrice() == null : this.getPurchasePrice().equals(other.getPurchasePrice()))
            && (this.getPurchaseNumber() == null ? other.getPurchaseNumber() == null : this.getPurchaseNumber().equals(other.getPurchaseNumber()))
            && (this.getPayAmount() == null ? other.getPayAmount() == null : this.getPayAmount().equals(other.getPayAmount()))
            && (this.getActualPayAmount() == null ? other.getActualPayAmount() == null : this.getActualPayAmount().equals(other.getActualPayAmount()))
            && (this.getActualPurchaseNumber() == null ? other.getActualPurchaseNumber() == null : this.getActualPurchaseNumber().equals(other.getActualPurchaseNumber()))
            && (this.getProductCategoryCode() == null ? other.getProductCategoryCode() == null : this.getProductCategoryCode().equals(other.getProductCategoryCode()))
            && (this.getProductCategoryName() == null ? other.getProductCategoryName() == null : this.getProductCategoryName().equals(other.getProductCategoryName()))
            && (this.getPictureUrls() == null ? other.getPictureUrls() == null : this.getPictureUrls().equals(other.getPictureUrls()))
            && (this.getWarrantyDesc() == null ? other.getWarrantyDesc() == null : this.getWarrantyDesc().equals(other.getWarrantyDesc()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getApplyUserId() == null ? other.getApplyUserId() == null : this.getApplyUserId().equals(other.getApplyUserId()))
            && (this.getApplyUserName() == null ? other.getApplyUserName() == null : this.getApplyUserName().equals(other.getApplyUserName()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNum() == null) ? 0 : getOrderNum().hashCode());
        result = prime * result + ((getOrderDetailNum() == null) ? 0 : getOrderDetailNum().hashCode());
        result = prime * result + ((getBrandCode() == null) ? 0 : getBrandCode().hashCode());
        result = prime * result + ((getBrandName() == null) ? 0 : getBrandName().hashCode());
        result = prime * result + ((getProductName() == null) ? 0 : getProductName().hashCode());
        result = prime * result + ((getProductSku() == null) ? 0 : getProductSku().hashCode());
        result = prime * result + ((getProductOutCode() == null) ? 0 : getProductOutCode().hashCode());
        result = prime * result + ((getPurchasePrice() == null) ? 0 : getPurchasePrice().hashCode());
        result = prime * result + ((getPurchaseNumber() == null) ? 0 : getPurchaseNumber().hashCode());
        result = prime * result + ((getPayAmount() == null) ? 0 : getPayAmount().hashCode());
        result = prime * result + ((getActualPayAmount() == null) ? 0 : getActualPayAmount().hashCode());
        result = prime * result + ((getActualPurchaseNumber() == null) ? 0 : getActualPurchaseNumber().hashCode());
        result = prime * result + ((getProductCategoryCode() == null) ? 0 : getProductCategoryCode().hashCode());
        result = prime * result + ((getProductCategoryName() == null) ? 0 : getProductCategoryName().hashCode());
        result = prime * result + ((getPictureUrls() == null) ? 0 : getPictureUrls().hashCode());
        result = prime * result + ((getWarrantyDesc() == null) ? 0 : getWarrantyDesc().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getApplyUserId() == null) ? 0 : getApplyUserId().hashCode());
        result = prime * result + ((getApplyUserName() == null) ? 0 : getApplyUserName().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderNum=").append(orderNum);
        sb.append(", orderDetailNum=").append(orderDetailNum);
        sb.append(", brandCode=").append(brandCode);
        sb.append(", brandName=").append(brandName);
        sb.append(", productName=").append(productName);
        sb.append(", productSku=").append(productSku);
        sb.append(", productOutCode=").append(productOutCode);
        sb.append(", purchasePrice=").append(purchasePrice);
        sb.append(", purchaseNumber=").append(purchaseNumber);
        sb.append(", payAmount=").append(payAmount);
        sb.append(", actualPayAmount=").append(actualPayAmount);
        sb.append(", actualPurchaseNumber=").append(actualPurchaseNumber);
        sb.append(", productCategoryCode=").append(productCategoryCode);
        sb.append(", productCategoryName=").append(productCategoryName);
        sb.append(", pictureUrls=").append(pictureUrls);
        sb.append(", warrantyDesc=").append(warrantyDesc);
        sb.append(", createTime=").append(createTime);
        sb.append(", applyUserId=").append(applyUserId);
        sb.append(", applyUserName=").append(applyUserName);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}