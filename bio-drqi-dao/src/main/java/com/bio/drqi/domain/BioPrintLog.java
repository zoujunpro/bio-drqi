package com.bio.drqi.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName bio_print_log
 */
public class BioPrintLog implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 打印场景
     */
    private String printType;

    /**
     * 打印参数
     */
    private String printParam;

    /**
     * 打印人
     */
    private String printUserName;

    /**
     * 打印事件
     */
    private Date printTime;

    /**
     * 打印状态
     */
    private String printStatus;

    /**
     * 打印数据
     */
    private String printData;

    /**
     * 打印编号（打印号左补到十位）
     */
    private String printCode;

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
     * 打印场景
     */
    public String getPrintType() {
        return printType;
    }

    /**
     * 打印场景
     */
    public void setPrintType(String printType) {
        this.printType = printType;
    }

    /**
     * 打印参数
     */
    public String getPrintParam() {
        return printParam;
    }

    /**
     * 打印参数
     */
    public void setPrintParam(String printParam) {
        this.printParam = printParam;
    }

    /**
     * 打印人
     */
    public String getPrintUserName() {
        return printUserName;
    }

    /**
     * 打印人
     */
    public void setPrintUserName(String printUserName) {
        this.printUserName = printUserName;
    }

    /**
     * 打印事件
     */
    public Date getPrintTime() {
        return printTime;
    }

    /**
     * 打印事件
     */
    public void setPrintTime(Date printTime) {
        this.printTime = printTime;
    }

    /**
     * 打印状态
     */
    public String getPrintStatus() {
        return printStatus;
    }

    /**
     * 打印状态
     */
    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    /**
     * 打印数据
     */
    public String getPrintData() {
        return printData;
    }

    /**
     * 打印数据
     */
    public void setPrintData(String printData) {
        this.printData = printData;
    }

    /**
     * 打印编号（打印号左补到十位）
     */
    public String getPrintCode() {
        return printCode;
    }

    /**
     * 打印编号（打印号左补到十位）
     */
    public void setPrintCode(String printCode) {
        this.printCode = printCode;
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
        BioPrintLog other = (BioPrintLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPrintType() == null ? other.getPrintType() == null : this.getPrintType().equals(other.getPrintType()))
            && (this.getPrintParam() == null ? other.getPrintParam() == null : this.getPrintParam().equals(other.getPrintParam()))
            && (this.getPrintUserName() == null ? other.getPrintUserName() == null : this.getPrintUserName().equals(other.getPrintUserName()))
            && (this.getPrintTime() == null ? other.getPrintTime() == null : this.getPrintTime().equals(other.getPrintTime()))
            && (this.getPrintStatus() == null ? other.getPrintStatus() == null : this.getPrintStatus().equals(other.getPrintStatus()))
            && (this.getPrintData() == null ? other.getPrintData() == null : this.getPrintData().equals(other.getPrintData()))
            && (this.getPrintCode() == null ? other.getPrintCode() == null : this.getPrintCode().equals(other.getPrintCode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPrintType() == null) ? 0 : getPrintType().hashCode());
        result = prime * result + ((getPrintParam() == null) ? 0 : getPrintParam().hashCode());
        result = prime * result + ((getPrintUserName() == null) ? 0 : getPrintUserName().hashCode());
        result = prime * result + ((getPrintTime() == null) ? 0 : getPrintTime().hashCode());
        result = prime * result + ((getPrintStatus() == null) ? 0 : getPrintStatus().hashCode());
        result = prime * result + ((getPrintData() == null) ? 0 : getPrintData().hashCode());
        result = prime * result + ((getPrintCode() == null) ? 0 : getPrintCode().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", printType=").append(printType);
        sb.append(", printParam=").append(printParam);
        sb.append(", printUserName=").append(printUserName);
        sb.append(", printTime=").append(printTime);
        sb.append(", printStatus=").append(printStatus);
        sb.append(", printData=").append(printData);
        sb.append(", printCode=").append(printCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}