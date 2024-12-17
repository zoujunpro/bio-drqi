package com.bio.drqi.domain;

import java.io.Serializable;

/**
 * 
 * @TableName bio_seed_label_tb
 */
public class BioSeedLabelTb implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 打印ID
     */
    private Long printId;

    /**
     * 种子打印标签类型 OUT 出库 ,IN入库
     */
    private String seedLabelType;

    /**
     * 种子编号
     */
    private String seedNum;

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
     * 打印ID
     */
    public Long getPrintId() {
        return printId;
    }

    /**
     * 打印ID
     */
    public void setPrintId(Long printId) {
        this.printId = printId;
    }

    /**
     * 种子打印标签类型 OUT 出库 ,IN入库
     */
    public String getSeedLabelType() {
        return seedLabelType;
    }

    /**
     * 种子打印标签类型 OUT 出库 ,IN入库
     */
    public void setSeedLabelType(String seedLabelType) {
        this.seedLabelType = seedLabelType;
    }

    /**
     * 种子编号
     */
    public String getSeedNum() {
        return seedNum;
    }

    /**
     * 种子编号
     */
    public void setSeedNum(String seedNum) {
        this.seedNum = seedNum;
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
        BioSeedLabelTb other = (BioSeedLabelTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPrintId() == null ? other.getPrintId() == null : this.getPrintId().equals(other.getPrintId()))
            && (this.getSeedLabelType() == null ? other.getSeedLabelType() == null : this.getSeedLabelType().equals(other.getSeedLabelType()))
            && (this.getSeedNum() == null ? other.getSeedNum() == null : this.getSeedNum().equals(other.getSeedNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPrintId() == null) ? 0 : getPrintId().hashCode());
        result = prime * result + ((getSeedLabelType() == null) ? 0 : getSeedLabelType().hashCode());
        result = prime * result + ((getSeedNum() == null) ? 0 : getSeedNum().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", printId=").append(printId);
        sb.append(", seedLabelType=").append(seedLabelType);
        sb.append(", seedNum=").append(seedNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}