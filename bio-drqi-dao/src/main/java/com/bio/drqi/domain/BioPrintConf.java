package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * 打印配置信息表
 * @TableName bio_print_conf
 */
public class BioPrintConf implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 打印类型
     */
    private String printType;

    /**
     * 打印说明
     */
    private String printDesc;

    /**
     * 打印案例地址
     */
    private String printExampleUrl;

    /**
     * 打印模板
     */
    private String printTemplate;

    /**
     * 扩展字段1
     */
    private String extendField1;

    /**
     * 扩展字段2
     */
    private String extendField2;

    /**
     * 是否展示
     */
    private String showFlag;

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
     * 打印类型
     */
    public String getPrintType() {
        return printType;
    }

    /**
     * 打印类型
     */
    public void setPrintType(String printType) {
        this.printType = printType;
    }

    /**
     * 打印说明
     */
    public String getPrintDesc() {
        return printDesc;
    }

    /**
     * 打印说明
     */
    public void setPrintDesc(String printDesc) {
        this.printDesc = printDesc;
    }

    /**
     * 打印案例地址
     */
    public String getPrintExampleUrl() {
        return printExampleUrl;
    }

    /**
     * 打印案例地址
     */
    public void setPrintExampleUrl(String printExampleUrl) {
        this.printExampleUrl = printExampleUrl;
    }

    /**
     * 打印模板
     */
    public String getPrintTemplate() {
        return printTemplate;
    }

    /**
     * 打印模板
     */
    public void setPrintTemplate(String printTemplate) {
        this.printTemplate = printTemplate;
    }

    /**
     * 扩展字段1
     */
    public String getExtendField1() {
        return extendField1;
    }

    /**
     * 扩展字段1
     */
    public void setExtendField1(String extendField1) {
        this.extendField1 = extendField1;
    }

    /**
     * 扩展字段2
     */
    public String getExtendField2() {
        return extendField2;
    }

    /**
     * 扩展字段2
     */
    public void setExtendField2(String extendField2) {
        this.extendField2 = extendField2;
    }

    /**
     * 是否展示
     */
    public String getShowFlag() {
        return showFlag;
    }

    /**
     * 是否展示
     */
    public void setShowFlag(String showFlag) {
        this.showFlag = showFlag;
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
        BioPrintConf other = (BioPrintConf) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPrintType() == null ? other.getPrintType() == null : this.getPrintType().equals(other.getPrintType()))
            && (this.getPrintDesc() == null ? other.getPrintDesc() == null : this.getPrintDesc().equals(other.getPrintDesc()))
            && (this.getPrintExampleUrl() == null ? other.getPrintExampleUrl() == null : this.getPrintExampleUrl().equals(other.getPrintExampleUrl()))
            && (this.getPrintTemplate() == null ? other.getPrintTemplate() == null : this.getPrintTemplate().equals(other.getPrintTemplate()))
            && (this.getExtendField1() == null ? other.getExtendField1() == null : this.getExtendField1().equals(other.getExtendField1()))
            && (this.getExtendField2() == null ? other.getExtendField2() == null : this.getExtendField2().equals(other.getExtendField2()))
            && (this.getShowFlag() == null ? other.getShowFlag() == null : this.getShowFlag().equals(other.getShowFlag()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPrintType() == null) ? 0 : getPrintType().hashCode());
        result = prime * result + ((getPrintDesc() == null) ? 0 : getPrintDesc().hashCode());
        result = prime * result + ((getPrintExampleUrl() == null) ? 0 : getPrintExampleUrl().hashCode());
        result = prime * result + ((getPrintTemplate() == null) ? 0 : getPrintTemplate().hashCode());
        result = prime * result + ((getExtendField1() == null) ? 0 : getExtendField1().hashCode());
        result = prime * result + ((getExtendField2() == null) ? 0 : getExtendField2().hashCode());
        result = prime * result + ((getShowFlag() == null) ? 0 : getShowFlag().hashCode());
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
        sb.append(", printDesc=").append(printDesc);
        sb.append(", printExampleUrl=").append(printExampleUrl);
        sb.append(", printTemplate=").append(printTemplate);
        sb.append(", extendField1=").append(extendField1);
        sb.append(", extendField2=").append(extendField2);
        sb.append(", showFlag=").append(showFlag);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}