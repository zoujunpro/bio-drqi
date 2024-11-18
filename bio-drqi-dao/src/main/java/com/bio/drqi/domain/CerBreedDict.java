package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 
 * @TableName cer_breed_dict
 */
@TableName(value ="cer_breed_dict")
public class CerBreedDict implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String breedCode;

    /**
     * 
     */
    private String breedName;

    /**
     * 物种
     */
    private String speciesCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     */
    public String getBreedCode() {
        return breedCode;
    }

    /**
     * 
     */
    public void setBreedCode(String breedCode) {
        this.breedCode = breedCode;
    }

    /**
     * 
     */
    public String getBreedName() {
        return breedName;
    }

    /**
     * 
     */
    public void setBreedName(String breedName) {
        this.breedName = breedName;
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
        CerBreedDict other = (CerBreedDict) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getBreedCode() == null ? other.getBreedCode() == null : this.getBreedCode().equals(other.getBreedCode()))
            && (this.getBreedName() == null ? other.getBreedName() == null : this.getBreedName().equals(other.getBreedName()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBreedCode() == null) ? 0 : getBreedCode().hashCode());
        result = prime * result + ((getBreedName() == null) ? 0 : getBreedName().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", breedCode=").append(breedCode);
        sb.append(", breedName=").append(breedName);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}