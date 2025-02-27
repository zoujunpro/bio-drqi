package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品信息表
 * @TableName bms_product_tb
 */
@TableName(value ="bms_product_tb")
public class BmsProductTb implements Serializable {
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
     * 商品内部编号
     */
    private String projectInnerCode;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;

    /**
     * 商品类型编号
     */
    private String productTypeCode;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 商品规格
     */
    private String productSku;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 删除标识
     */
    private String deleteFlag;

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
     * 商品内部编号
     */
    public String getProjectInnerCode() {
        return projectInnerCode;
    }

    /**
     * 商品内部编号
     */
    public void setProjectInnerCode(String projectInnerCode) {
        this.projectInnerCode = projectInnerCode;
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
     * 商品类型编号
     */
    public String getProductTypeCode() {
        return productTypeCode;
    }

    /**
     * 商品类型编号
     */
    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    /**
     * 供应商编号
     */
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * 供应商编号
     */
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
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
     * 创建日期
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建日期
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建人ID
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * 创建人ID
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 创建人名称
     */
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * 创建人名称
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    /**
     * 删除标识
     */
    public String getDeleteFlag() {
        return deleteFlag;
    }

    /**
     * 删除标识
     */
    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
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
        BmsProductTb other = (BmsProductTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProductName() == null ? other.getProductName() == null : this.getProductName().equals(other.getProductName()))
            && (this.getProductOutCode() == null ? other.getProductOutCode() == null : this.getProductOutCode().equals(other.getProductOutCode()))
            && (this.getProjectInnerCode() == null ? other.getProjectInnerCode() == null : this.getProjectInnerCode().equals(other.getProjectInnerCode()))
            && (this.getProductCategoryCode() == null ? other.getProductCategoryCode() == null : this.getProductCategoryCode().equals(other.getProductCategoryCode()))
            && (this.getProductTypeCode() == null ? other.getProductTypeCode() == null : this.getProductTypeCode().equals(other.getProductTypeCode()))
            && (this.getSupplierCode() == null ? other.getSupplierCode() == null : this.getSupplierCode().equals(other.getSupplierCode()))
            && (this.getBrandName() == null ? other.getBrandName() == null : this.getBrandName().equals(other.getBrandName()))
            && (this.getBrandCode() == null ? other.getBrandCode() == null : this.getBrandCode().equals(other.getBrandCode()))
            && (this.getProductSku() == null ? other.getProductSku() == null : this.getProductSku().equals(other.getProductSku()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getDeleteFlag() == null ? other.getDeleteFlag() == null : this.getDeleteFlag().equals(other.getDeleteFlag()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProductName() == null) ? 0 : getProductName().hashCode());
        result = prime * result + ((getProductOutCode() == null) ? 0 : getProductOutCode().hashCode());
        result = prime * result + ((getProjectInnerCode() == null) ? 0 : getProjectInnerCode().hashCode());
        result = prime * result + ((getProductCategoryCode() == null) ? 0 : getProductCategoryCode().hashCode());
        result = prime * result + ((getProductTypeCode() == null) ? 0 : getProductTypeCode().hashCode());
        result = prime * result + ((getSupplierCode() == null) ? 0 : getSupplierCode().hashCode());
        result = prime * result + ((getBrandName() == null) ? 0 : getBrandName().hashCode());
        result = prime * result + ((getBrandCode() == null) ? 0 : getBrandCode().hashCode());
        result = prime * result + ((getProductSku() == null) ? 0 : getProductSku().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        result = prime * result + ((getDeleteFlag() == null) ? 0 : getDeleteFlag().hashCode());
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
        sb.append(", projectInnerCode=").append(projectInnerCode);
        sb.append(", productCategoryCode=").append(productCategoryCode);
        sb.append(", productTypeCode=").append(productTypeCode);
        sb.append(", supplierCode=").append(supplierCode);
        sb.append(", brandName=").append(brandName);
        sb.append(", brandCode=").append(brandCode);
        sb.append(", productSku=").append(productSku);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", deleteFlag=").append(deleteFlag);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}