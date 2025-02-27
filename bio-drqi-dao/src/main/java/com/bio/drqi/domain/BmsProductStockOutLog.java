package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 出库记录日志表
 * @TableName bms_product_stock_out_log
 */
@TableName(value ="bms_product_stock_out_log")
public class BmsProductStockOutLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

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
    private String productSku;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 库存ID
     */
    private Integer stockId;

    /**
     * 研发项目
     */
    private String projectCode;

    /**
     * 出库数量
     */
    private Integer outNumber;

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
     * 出库备注
     */
    private String remark;

    /**
     * 出库类型 1正常出库 2退货出库
     */
    private String outType;

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
     * 库存ID
     */
    public Integer getStockId() {
        return stockId;
    }

    /**
     * 库存ID
     */
    public void setStockId(Integer stockId) {
        this.stockId = stockId;
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
     * 出库数量
     */
    public Integer getOutNumber() {
        return outNumber;
    }

    /**
     * 出库数量
     */
    public void setOutNumber(Integer outNumber) {
        this.outNumber = outNumber;
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
     * 出库备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 出库备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 出库类型 1正常出库 2退货出库
     */
    public String getOutType() {
        return outType;
    }

    /**
     * 出库类型 1正常出库 2退货出库
     */
    public void setOutType(String outType) {
        this.outType = outType;
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
        BmsProductStockOutLog other = (BmsProductStockOutLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProductName() == null ? other.getProductName() == null : this.getProductName().equals(other.getProductName()))
            && (this.getProductOutCode() == null ? other.getProductOutCode() == null : this.getProductOutCode().equals(other.getProductOutCode()))
            && (this.getProductCategoryCode() == null ? other.getProductCategoryCode() == null : this.getProductCategoryCode().equals(other.getProductCategoryCode()))
            && (this.getProductCategoryName() == null ? other.getProductCategoryName() == null : this.getProductCategoryName().equals(other.getProductCategoryName()))
            && (this.getBrandCode() == null ? other.getBrandCode() == null : this.getBrandCode().equals(other.getBrandCode()))
            && (this.getBrandName() == null ? other.getBrandName() == null : this.getBrandName().equals(other.getBrandName()))
            && (this.getProductSku() == null ? other.getProductSku() == null : this.getProductSku().equals(other.getProductSku()))
            && (this.getBatchNo() == null ? other.getBatchNo() == null : this.getBatchNo().equals(other.getBatchNo()))
            && (this.getStockId() == null ? other.getStockId() == null : this.getStockId().equals(other.getStockId()))
            && (this.getProjectCode() == null ? other.getProjectCode() == null : this.getProjectCode().equals(other.getProjectCode()))
            && (this.getOutNumber() == null ? other.getOutNumber() == null : this.getOutNumber().equals(other.getOutNumber()))
            && (this.getApplyUserId() == null ? other.getApplyUserId() == null : this.getApplyUserId().equals(other.getApplyUserId()))
            && (this.getApplyUserName() == null ? other.getApplyUserName() == null : this.getApplyUserName().equals(other.getApplyUserName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getOutType() == null ? other.getOutType() == null : this.getOutType().equals(other.getOutType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProductName() == null) ? 0 : getProductName().hashCode());
        result = prime * result + ((getProductOutCode() == null) ? 0 : getProductOutCode().hashCode());
        result = prime * result + ((getProductCategoryCode() == null) ? 0 : getProductCategoryCode().hashCode());
        result = prime * result + ((getProductCategoryName() == null) ? 0 : getProductCategoryName().hashCode());
        result = prime * result + ((getBrandCode() == null) ? 0 : getBrandCode().hashCode());
        result = prime * result + ((getBrandName() == null) ? 0 : getBrandName().hashCode());
        result = prime * result + ((getProductSku() == null) ? 0 : getProductSku().hashCode());
        result = prime * result + ((getBatchNo() == null) ? 0 : getBatchNo().hashCode());
        result = prime * result + ((getStockId() == null) ? 0 : getStockId().hashCode());
        result = prime * result + ((getProjectCode() == null) ? 0 : getProjectCode().hashCode());
        result = prime * result + ((getOutNumber() == null) ? 0 : getOutNumber().hashCode());
        result = prime * result + ((getApplyUserId() == null) ? 0 : getApplyUserId().hashCode());
        result = prime * result + ((getApplyUserName() == null) ? 0 : getApplyUserName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getOutType() == null) ? 0 : getOutType().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productName=").append(productName);
        sb.append(", productOutCode=").append(productOutCode);
        sb.append(", productCategoryCode=").append(productCategoryCode);
        sb.append(", productCategoryName=").append(productCategoryName);
        sb.append(", brandCode=").append(brandCode);
        sb.append(", brandName=").append(brandName);
        sb.append(", productSku=").append(productSku);
        sb.append(", batchNo=").append(batchNo);
        sb.append(", stockId=").append(stockId);
        sb.append(", projectCode=").append(projectCode);
        sb.append(", outNumber=").append(outNumber);
        sb.append(", applyUserId=").append(applyUserId);
        sb.append(", applyUserName=").append(applyUserName);
        sb.append(", createTime=").append(createTime);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", remark=").append(remark);
        sb.append(", outType=").append(outType);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}