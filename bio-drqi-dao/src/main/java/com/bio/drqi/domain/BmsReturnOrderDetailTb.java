package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 退货订单明细表
 * @TableName bms_return_order_detail_tb
 */
@TableName(value ="bms_return_order_detail_tb")
public class BmsReturnOrderDetailTb implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 子订单编号
     */
    private String orderDetailNum;

    /**
     * 退货数量
     */
    private Integer returnNumber;

    /**
     * 退货金额
     */
    private BigDecimal returnAmount;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品单价
     */
    private BigDecimal productPrice;

    /**
     * 退货备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
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
     * 退货数量
     */
    public Integer getReturnNumber() {
        return returnNumber;
    }

    /**
     * 退货数量
     */
    public void setReturnNumber(Integer returnNumber) {
        this.returnNumber = returnNumber;
    }

    /**
     * 退货金额
     */
    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    /**
     * 退货金额
     */
    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
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
     * 商品单价
     */
    public BigDecimal getProductPrice() {
        return productPrice;
    }

    /**
     * 商品单价
     */
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * 退货备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 退货备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
        BmsReturnOrderDetailTb other = (BmsReturnOrderDetailTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderDetailNum() == null ? other.getOrderDetailNum() == null : this.getOrderDetailNum().equals(other.getOrderDetailNum()))
            && (this.getReturnNumber() == null ? other.getReturnNumber() == null : this.getReturnNumber().equals(other.getReturnNumber()))
            && (this.getReturnAmount() == null ? other.getReturnAmount() == null : this.getReturnAmount().equals(other.getReturnAmount()))
            && (this.getApplyUserId() == null ? other.getApplyUserId() == null : this.getApplyUserId().equals(other.getApplyUserId()))
            && (this.getApplyUserName() == null ? other.getApplyUserName() == null : this.getApplyUserName().equals(other.getApplyUserName()))
            && (this.getProductName() == null ? other.getProductName() == null : this.getProductName().equals(other.getProductName()))
            && (this.getProductPrice() == null ? other.getProductPrice() == null : this.getProductPrice().equals(other.getProductPrice()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderDetailNum() == null) ? 0 : getOrderDetailNum().hashCode());
        result = prime * result + ((getReturnNumber() == null) ? 0 : getReturnNumber().hashCode());
        result = prime * result + ((getReturnAmount() == null) ? 0 : getReturnAmount().hashCode());
        result = prime * result + ((getApplyUserId() == null) ? 0 : getApplyUserId().hashCode());
        result = prime * result + ((getApplyUserName() == null) ? 0 : getApplyUserName().hashCode());
        result = prime * result + ((getProductName() == null) ? 0 : getProductName().hashCode());
        result = prime * result + ((getProductPrice() == null) ? 0 : getProductPrice().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
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
        sb.append(", returnNumber=").append(returnNumber);
        sb.append(", returnAmount=").append(returnAmount);
        sb.append(", applyUserId=").append(applyUserId);
        sb.append(", applyUserName=").append(applyUserName);
        sb.append(", productName=").append(productName);
        sb.append(", productPrice=").append(productPrice);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}