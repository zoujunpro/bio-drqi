package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @TableName bms_product_stock_tb
 */
@TableName(value ="bms_product_stock_tb")
public class BmsProductStockTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
    private String productSpecs;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 累计入库数量
     */
    private Integer totalStoreNumber;

    /**
     * 当前库存数量
     */
    private Integer currentStockNumber;

    /**
     * 累计出库数量
     */
    private Integer totalOutNumber;

    /**
     * 货品单价
     */
    private BigDecimal productPrice;

    /**
     * 库名称
     */
    private String stockName;

    /**
     * 库编号
     */
    private String stockCode;

    /**
     * 库存位置编号
     */
    private String stockLocationNumber;

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
     * 累计入库数量
     */
    public Integer getTotalStoreNumber() {
        return totalStoreNumber;
    }

    /**
     * 累计入库数量
     */
    public void setTotalStoreNumber(Integer totalStoreNumber) {
        this.totalStoreNumber = totalStoreNumber;
    }

    /**
     * 当前库存数量
     */
    public Integer getCurrentStockNumber() {
        return currentStockNumber;
    }

    /**
     * 当前库存数量
     */
    public void setCurrentStockNumber(Integer currentStockNumber) {
        this.currentStockNumber = currentStockNumber;
    }

    /**
     * 累计出库数量
     */
    public Integer getTotalOutNumber() {
        return totalOutNumber;
    }

    /**
     * 累计出库数量
     */
    public void setTotalOutNumber(Integer totalOutNumber) {
        this.totalOutNumber = totalOutNumber;
    }

    /**
     * 货品单价
     */
    public BigDecimal getProductPrice() {
        return productPrice;
    }

    /**
     * 货品单价
     */
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * 库名称
     */
    public String getStockName() {
        return stockName;
    }

    /**
     * 库名称
     */
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    /**
     * 库编号
     */
    public String getStockCode() {
        return stockCode;
    }

    /**
     * 库编号
     */
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
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
        BmsProductStockTb other = (BmsProductStockTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProductName() == null ? other.getProductName() == null : this.getProductName().equals(other.getProductName()))
            && (this.getProductOutCode() == null ? other.getProductOutCode() == null : this.getProductOutCode().equals(other.getProductOutCode()))
            && (this.getProductCategoryCode() == null ? other.getProductCategoryCode() == null : this.getProductCategoryCode().equals(other.getProductCategoryCode()))
            && (this.getProductCategoryName() == null ? other.getProductCategoryName() == null : this.getProductCategoryName().equals(other.getProductCategoryName()))
            && (this.getBrandCode() == null ? other.getBrandCode() == null : this.getBrandCode().equals(other.getBrandCode()))
            && (this.getBrandName() == null ? other.getBrandName() == null : this.getBrandName().equals(other.getBrandName()))
            && (this.getProductSpecs() == null ? other.getProductSpecs() == null : this.getProductSpecs().equals(other.getProductSpecs()))
            && (this.getBatchNo() == null ? other.getBatchNo() == null : this.getBatchNo().equals(other.getBatchNo()))
            && (this.getTotalStoreNumber() == null ? other.getTotalStoreNumber() == null : this.getTotalStoreNumber().equals(other.getTotalStoreNumber()))
            && (this.getCurrentStockNumber() == null ? other.getCurrentStockNumber() == null : this.getCurrentStockNumber().equals(other.getCurrentStockNumber()))
            && (this.getTotalOutNumber() == null ? other.getTotalOutNumber() == null : this.getTotalOutNumber().equals(other.getTotalOutNumber()))
            && (this.getProductPrice() == null ? other.getProductPrice() == null : this.getProductPrice().equals(other.getProductPrice()))
            && (this.getStockName() == null ? other.getStockName() == null : this.getStockName().equals(other.getStockName()))
            && (this.getStockCode() == null ? other.getStockCode() == null : this.getStockCode().equals(other.getStockCode()))
            && (this.getStockLocationNumber() == null ? other.getStockLocationNumber() == null : this.getStockLocationNumber().equals(other.getStockLocationNumber()));
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
        result = prime * result + ((getProductSpecs() == null) ? 0 : getProductSpecs().hashCode());
        result = prime * result + ((getBatchNo() == null) ? 0 : getBatchNo().hashCode());
        result = prime * result + ((getTotalStoreNumber() == null) ? 0 : getTotalStoreNumber().hashCode());
        result = prime * result + ((getCurrentStockNumber() == null) ? 0 : getCurrentStockNumber().hashCode());
        result = prime * result + ((getTotalOutNumber() == null) ? 0 : getTotalOutNumber().hashCode());
        result = prime * result + ((getProductPrice() == null) ? 0 : getProductPrice().hashCode());
        result = prime * result + ((getStockName() == null) ? 0 : getStockName().hashCode());
        result = prime * result + ((getStockCode() == null) ? 0 : getStockCode().hashCode());
        result = prime * result + ((getStockLocationNumber() == null) ? 0 : getStockLocationNumber().hashCode());
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
        sb.append(", productSpecs=").append(productSpecs);
        sb.append(", batchNo=").append(batchNo);
        sb.append(", totalStoreNumber=").append(totalStoreNumber);
        sb.append(", currentStockNumber=").append(currentStockNumber);
        sb.append(", totalOutNumber=").append(totalOutNumber);
        sb.append(", productPrice=").append(productPrice);
        sb.append(", stockName=").append(stockName);
        sb.append(", stockCode=").append(stockCode);
        sb.append(", stockLocationNumber=").append(stockLocationNumber);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}