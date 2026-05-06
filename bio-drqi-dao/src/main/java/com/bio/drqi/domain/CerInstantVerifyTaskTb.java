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
 * 瞬时验证任务
 * @TableName cer_instant_verify_task_tb
 */
@TableName(value ="cer_instant_verify_task_tb")
public class CerInstantVerifyTaskTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 实施方案ID
     */
    private Integer vectorTaskId;

    /**
     * 任务编号
     */
    private String vectorTaskCode;

    /**
     * 瞬时验证编号
     */
    private String instantVerifyCode;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 内容
     */
    @EsFieldMapping(type = EsFieldTypeEnum.TEXT, index = false)
    private String textJson;

    /**
     * 创建时间
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
     * 项目ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 项目ID
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 子项目ID
     */
    public Integer getSubProjectId() {
        return subProjectId;
    }

    /**
     * 子项目ID
     */
    public void setSubProjectId(Integer subProjectId) {
        this.subProjectId = subProjectId;
    }

    /**
     * 实施方案ID
     */
    public Integer getVectorTaskId() {
        return vectorTaskId;
    }

    /**
     * 实施方案ID
     */
    public void setVectorTaskId(Integer vectorTaskId) {
        this.vectorTaskId = vectorTaskId;
    }

    /**
     * 任务编号
     */
    public String getVectorTaskCode() {
        return vectorTaskCode;
    }

    /**
     * 任务编号
     */
    public void setVectorTaskCode(String vectorTaskCode) {
        this.vectorTaskCode = vectorTaskCode;
    }

    /**
     * 瞬时验证编号
     */
    public String getInstantVerifyCode() {
        return instantVerifyCode;
    }

    /**
     * 瞬时验证编号
     */
    public void setInstantVerifyCode(String instantVerifyCode) {
        this.instantVerifyCode = instantVerifyCode;
    }

    /**
     * 项目编号
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 项目编号
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * 子项目编号
     */
    public String getSubProjectCode() {
        return subProjectCode;
    }

    /**
     * 子项目编号
     */
    public void setSubProjectCode(String subProjectCode) {
        this.subProjectCode = subProjectCode;
    }

    /**
     * 内容
     */
    public String getTextJson() {
        return textJson;
    }

    /**
     * 内容
     */
    public void setTextJson(String textJson) {
        this.textJson = textJson;
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
        CerInstantVerifyTaskTb other = (CerInstantVerifyTaskTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProjectId() == null ? other.getProjectId() == null : this.getProjectId().equals(other.getProjectId()))
            && (this.getSubProjectId() == null ? other.getSubProjectId() == null : this.getSubProjectId().equals(other.getSubProjectId()))
            && (this.getVectorTaskId() == null ? other.getVectorTaskId() == null : this.getVectorTaskId().equals(other.getVectorTaskId()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getInstantVerifyCode() == null ? other.getInstantVerifyCode() == null : this.getInstantVerifyCode().equals(other.getInstantVerifyCode()))
            && (this.getProjectCode() == null ? other.getProjectCode() == null : this.getProjectCode().equals(other.getProjectCode()))
            && (this.getSubProjectCode() == null ? other.getSubProjectCode() == null : this.getSubProjectCode().equals(other.getSubProjectCode()))
            && (this.getTextJson() == null ? other.getTextJson() == null : this.getTextJson().equals(other.getTextJson()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProjectId() == null) ? 0 : getProjectId().hashCode());
        result = prime * result + ((getSubProjectId() == null) ? 0 : getSubProjectId().hashCode());
        result = prime * result + ((getVectorTaskId() == null) ? 0 : getVectorTaskId().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getInstantVerifyCode() == null) ? 0 : getInstantVerifyCode().hashCode());
        result = prime * result + ((getProjectCode() == null) ? 0 : getProjectCode().hashCode());
        result = prime * result + ((getSubProjectCode() == null) ? 0 : getSubProjectCode().hashCode());
        result = prime * result + ((getTextJson() == null) ? 0 : getTextJson().hashCode());
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
        sb.append(", projectId=").append(projectId);
        sb.append(", subProjectId=").append(subProjectId);
        sb.append(", vectorTaskId=").append(vectorTaskId);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", instantVerifyCode=").append(instantVerifyCode);
        sb.append(", projectCode=").append(projectCode);
        sb.append(", subProjectCode=").append(subProjectCode);
        sb.append(", textJson=").append(textJson);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
