package com.bio.drqi.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName bio_print_layout_tb
 */
public class BioPrintLayoutTb implements Serializable {
    /**
     * 主键iD
     */
    private Integer id;

    /**
     * 打印编号
     */
    private Long printId;

    /**
     * 版号
     */
    private String layoutNumber;

    /**
     * 打印时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

    /**
     * 主键iD
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键iD
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 打印编号
     */
    public Long getPrintId() {
        return printId;
    }

    /**
     * 打印编号
     */
    public void setPrintId(Long printId) {
        this.printId = printId;
    }

    /**
     * 版号
     */
    public String getLayoutNumber() {
        return layoutNumber;
    }

    /**
     * 版号
     */
    public void setLayoutNumber(String layoutNumber) {
        this.layoutNumber = layoutNumber;
    }

    /**
     * 打印时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 打印时间
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
        BioPrintLayoutTb other = (BioPrintLayoutTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPrintId() == null ? other.getPrintId() == null : this.getPrintId().equals(other.getPrintId()))
            && (this.getLayoutNumber() == null ? other.getLayoutNumber() == null : this.getLayoutNumber().equals(other.getLayoutNumber()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPrintId() == null) ? 0 : getPrintId().hashCode());
        result = prime * result + ((getLayoutNumber() == null) ? 0 : getLayoutNumber().hashCode());
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
        sb.append(", printId=").append(printId);
        sb.append(", layoutNumber=").append(layoutNumber);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}