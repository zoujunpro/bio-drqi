package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 取样检测申请表
 * @TableName plant_sample_apply_tb
 */
@TableName(value ="plant_sample_apply_tb")
public class PlantSampleApplyTb implements Serializable {
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
     * 取样申请数量
     */
    private Integer applyNumber;

    /**
     * 取样申请时间
     */
    private Date applyTime;

    /**
     * 取样申请人ID
     */
    private Integer applyUserId;

    /**
     * 取样申请人
     */
    private String applyUserName;

    /**
     * 取样工单描述
     */
    private String applyDesc;

    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;

    /**
     * 鉴定引物地址
     */
    private String identifyExcelUrl;

    /**
     * 一代测序文件地址
     */
    private String oneTestExcelUrl;

    /**
     * NGS测序文件地址
     */
    private String ngsExcelUrl;

    /**
     * 是否是克隆苗取样 Y N
     */
    private String cloneFlag;

    /**
     * 孔板类型 one,more
     */
    private String layoutFlag;

    /**
     * 实施方案编号
     */
    private String vectorTaskCodes;

    /**
     * 取样编号范围
     */
    private String sampleCodeRange;

    /**
     * 任务状态
     */
    private String taskStatus;

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
     * 取样申请数量
     */
    public Integer getApplyNumber() {
        return applyNumber;
    }

    /**
     * 取样申请数量
     */
    public void setApplyNumber(Integer applyNumber) {
        this.applyNumber = applyNumber;
    }

    /**
     * 取样申请时间
     */
    public Date getApplyTime() {
        return applyTime;
    }

    /**
     * 取样申请时间
     */
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    /**
     * 取样申请人ID
     */
    public Integer getApplyUserId() {
        return applyUserId;
    }

    /**
     * 取样申请人ID
     */
    public void setApplyUserId(Integer applyUserId) {
        this.applyUserId = applyUserId;
    }

    /**
     * 取样申请人
     */
    public String getApplyUserName() {
        return applyUserName;
    }

    /**
     * 取样申请人
     */
    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    /**
     * 取样工单描述
     */
    public String getApplyDesc() {
        return applyDesc;
    }

    /**
     * 取样工单描述
     */
    public void setApplyDesc(String applyDesc) {
        this.applyDesc = applyDesc;
    }

    /**
     * 取样类型 F首次取样   R重复取样
     */
    public String getApplyType() {
        return applyType;
    }

    /**
     * 取样类型 F首次取样   R重复取样
     */
    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    /**
     * 鉴定引物地址
     */
    public String getIdentifyExcelUrl() {
        return identifyExcelUrl;
    }

    /**
     * 鉴定引物地址
     */
    public void setIdentifyExcelUrl(String identifyExcelUrl) {
        this.identifyExcelUrl = identifyExcelUrl;
    }

    /**
     * 一代测序文件地址
     */
    public String getOneTestExcelUrl() {
        return oneTestExcelUrl;
    }

    /**
     * 一代测序文件地址
     */
    public void setOneTestExcelUrl(String oneTestExcelUrl) {
        this.oneTestExcelUrl = oneTestExcelUrl;
    }

    /**
     * NGS测序文件地址
     */
    public String getNgsExcelUrl() {
        return ngsExcelUrl;
    }

    /**
     * NGS测序文件地址
     */
    public void setNgsExcelUrl(String ngsExcelUrl) {
        this.ngsExcelUrl = ngsExcelUrl;
    }

    /**
     * 是否是克隆苗取样 Y N
     */
    public String getCloneFlag() {
        return cloneFlag;
    }

    /**
     * 是否是克隆苗取样 Y N
     */
    public void setCloneFlag(String cloneFlag) {
        this.cloneFlag = cloneFlag;
    }

    /**
     * 孔板类型 one,more
     */
    public String getLayoutFlag() {
        return layoutFlag;
    }

    /**
     * 孔板类型 one,more
     */
    public void setLayoutFlag(String layoutFlag) {
        this.layoutFlag = layoutFlag;
    }

    /**
     * 实施方案编号
     */
    public String getVectorTaskCodes() {
        return vectorTaskCodes;
    }

    /**
     * 实施方案编号
     */
    public void setVectorTaskCodes(String vectorTaskCodes) {
        this.vectorTaskCodes = vectorTaskCodes;
    }

    /**
     * 取样编号范围
     */
    public String getSampleCodeRange() {
        return sampleCodeRange;
    }

    /**
     * 取样编号范围
     */
    public void setSampleCodeRange(String sampleCodeRange) {
        this.sampleCodeRange = sampleCodeRange;
    }

    /**
     * 任务状态
     */
    public String getTaskStatus() {
        return taskStatus;
    }

    /**
     * 任务状态
     */
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
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
        PlantSampleApplyTb other = (PlantSampleApplyTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getApplyNo() == null ? other.getApplyNo() == null : this.getApplyNo().equals(other.getApplyNo()))
            && (this.getApplyNumber() == null ? other.getApplyNumber() == null : this.getApplyNumber().equals(other.getApplyNumber()))
            && (this.getApplyTime() == null ? other.getApplyTime() == null : this.getApplyTime().equals(other.getApplyTime()))
            && (this.getApplyUserId() == null ? other.getApplyUserId() == null : this.getApplyUserId().equals(other.getApplyUserId()))
            && (this.getApplyUserName() == null ? other.getApplyUserName() == null : this.getApplyUserName().equals(other.getApplyUserName()))
            && (this.getApplyDesc() == null ? other.getApplyDesc() == null : this.getApplyDesc().equals(other.getApplyDesc()))
            && (this.getApplyType() == null ? other.getApplyType() == null : this.getApplyType().equals(other.getApplyType()))
            && (this.getIdentifyExcelUrl() == null ? other.getIdentifyExcelUrl() == null : this.getIdentifyExcelUrl().equals(other.getIdentifyExcelUrl()))
            && (this.getOneTestExcelUrl() == null ? other.getOneTestExcelUrl() == null : this.getOneTestExcelUrl().equals(other.getOneTestExcelUrl()))
            && (this.getNgsExcelUrl() == null ? other.getNgsExcelUrl() == null : this.getNgsExcelUrl().equals(other.getNgsExcelUrl()))
            && (this.getCloneFlag() == null ? other.getCloneFlag() == null : this.getCloneFlag().equals(other.getCloneFlag()))
            && (this.getLayoutFlag() == null ? other.getLayoutFlag() == null : this.getLayoutFlag().equals(other.getLayoutFlag()))
            && (this.getVectorTaskCodes() == null ? other.getVectorTaskCodes() == null : this.getVectorTaskCodes().equals(other.getVectorTaskCodes()))
            && (this.getSampleCodeRange() == null ? other.getSampleCodeRange() == null : this.getSampleCodeRange().equals(other.getSampleCodeRange()))
            && (this.getTaskStatus() == null ? other.getTaskStatus() == null : this.getTaskStatus().equals(other.getTaskStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getApplyNo() == null) ? 0 : getApplyNo().hashCode());
        result = prime * result + ((getApplyNumber() == null) ? 0 : getApplyNumber().hashCode());
        result = prime * result + ((getApplyTime() == null) ? 0 : getApplyTime().hashCode());
        result = prime * result + ((getApplyUserId() == null) ? 0 : getApplyUserId().hashCode());
        result = prime * result + ((getApplyUserName() == null) ? 0 : getApplyUserName().hashCode());
        result = prime * result + ((getApplyDesc() == null) ? 0 : getApplyDesc().hashCode());
        result = prime * result + ((getApplyType() == null) ? 0 : getApplyType().hashCode());
        result = prime * result + ((getIdentifyExcelUrl() == null) ? 0 : getIdentifyExcelUrl().hashCode());
        result = prime * result + ((getOneTestExcelUrl() == null) ? 0 : getOneTestExcelUrl().hashCode());
        result = prime * result + ((getNgsExcelUrl() == null) ? 0 : getNgsExcelUrl().hashCode());
        result = prime * result + ((getCloneFlag() == null) ? 0 : getCloneFlag().hashCode());
        result = prime * result + ((getLayoutFlag() == null) ? 0 : getLayoutFlag().hashCode());
        result = prime * result + ((getVectorTaskCodes() == null) ? 0 : getVectorTaskCodes().hashCode());
        result = prime * result + ((getSampleCodeRange() == null) ? 0 : getSampleCodeRange().hashCode());
        result = prime * result + ((getTaskStatus() == null) ? 0 : getTaskStatus().hashCode());
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
        sb.append(", applyNumber=").append(applyNumber);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", applyUserId=").append(applyUserId);
        sb.append(", applyUserName=").append(applyUserName);
        sb.append(", applyDesc=").append(applyDesc);
        sb.append(", applyType=").append(applyType);
        sb.append(", identifyExcelUrl=").append(identifyExcelUrl);
        sb.append(", oneTestExcelUrl=").append(oneTestExcelUrl);
        sb.append(", ngsExcelUrl=").append(ngsExcelUrl);
        sb.append(", cloneFlag=").append(cloneFlag);
        sb.append(", layoutFlag=").append(layoutFlag);
        sb.append(", vectorTaskCodes=").append(vectorTaskCodes);
        sb.append(", sampleCodeRange=").append(sampleCodeRange);
        sb.append(", taskStatus=").append(taskStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}