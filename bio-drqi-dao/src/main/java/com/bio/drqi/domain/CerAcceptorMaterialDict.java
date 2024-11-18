package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 
 * @TableName cer_acceptor_material_dict
 */
@TableName(value ="cer_acceptor_material_dict")
public class CerAcceptorMaterialDict implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 受体材料名称
     */
    private String acceptorMaterialName;

    /**
     * 受体材料编码
     */
    private String acceptorMaterialCode;

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
     * 物种
     */
    public String getSpeciesCode() {
        return speciesCode;
    }

    /**
     * 物种
     */
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    /**
     * 受体材料名称
     */
    public String getAcceptorMaterialName() {
        return acceptorMaterialName;
    }

    /**
     * 受体材料名称
     */
    public void setAcceptorMaterialName(String acceptorMaterialName) {
        this.acceptorMaterialName = acceptorMaterialName;
    }

    /**
     * 受体材料编码
     */
    public String getAcceptorMaterialCode() {
        return acceptorMaterialCode;
    }

    /**
     * 受体材料编码
     */
    public void setAcceptorMaterialCode(String acceptorMaterialCode) {
        this.acceptorMaterialCode = acceptorMaterialCode;
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
        CerAcceptorMaterialDict other = (CerAcceptorMaterialDict) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getAcceptorMaterialName() == null ? other.getAcceptorMaterialName() == null : this.getAcceptorMaterialName().equals(other.getAcceptorMaterialName()))
            && (this.getAcceptorMaterialCode() == null ? other.getAcceptorMaterialCode() == null : this.getAcceptorMaterialCode().equals(other.getAcceptorMaterialCode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getAcceptorMaterialName() == null) ? 0 : getAcceptorMaterialName().hashCode());
        result = prime * result + ((getAcceptorMaterialCode() == null) ? 0 : getAcceptorMaterialCode().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", acceptorMaterialName=").append(acceptorMaterialName);
        sb.append(", acceptorMaterialCode=").append(acceptorMaterialCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}