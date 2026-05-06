package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bio.drqi.common.annotation.EsFieldMapping;
import com.bio.drqi.common.enums.EsFieldTypeEnum;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName tc_sample_layout_tb
 */
@TableName(value ="tc_sample_layout_tb")
public class TcSampleLayoutTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 取样申请编号
     */
    private String applyNo;

    /**
     * 单管集合
     */
    @EsFieldMapping(type = EsFieldTypeEnum.TEXT, index = false)
    private String singleContent;

    /**
     * 板集合
     */
    @EsFieldMapping(type = EsFieldTypeEnum.TEXT, index = false)
    private String plateContent;

    /**
     * 更新日期
     */
    private Date createTime;

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
     * 取样申请编号
     */
    public String getApplyNo() {
        return applyNo;
    }

    /**
     * 取样申请编号
     */
    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    /**
     * 单管集合
     */
    public String getSingleContent() {
        return singleContent;
    }

    /**
     * 单管集合
     */
    public void setSingleContent(String singleContent) {
        this.singleContent = singleContent;
    }

    /**
     * 板集合
     */
    public String getPlateContent() {
        return plateContent;
    }

    /**
     * 板集合
     */
    public void setPlateContent(String plateContent) {
        this.plateContent = plateContent;
    }

    /**
     * 更新日期
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 更新日期
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
        TcSampleLayoutTb other = (TcSampleLayoutTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getApplyNo() == null ? other.getApplyNo() == null : this.getApplyNo().equals(other.getApplyNo()))
            && (this.getSingleContent() == null ? other.getSingleContent() == null : this.getSingleContent().equals(other.getSingleContent()))
            && (this.getPlateContent() == null ? other.getPlateContent() == null : this.getPlateContent().equals(other.getPlateContent()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getApplyNo() == null) ? 0 : getApplyNo().hashCode());
        result = prime * result + ((getSingleContent() == null) ? 0 : getSingleContent().hashCode());
        result = prime * result + ((getPlateContent() == null) ? 0 : getPlateContent().hashCode());
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
        sb.append(", applyNo=").append(applyNo);
        sb.append(", singleContent=").append(singleContent);
        sb.append(", plateContent=").append(plateContent);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
