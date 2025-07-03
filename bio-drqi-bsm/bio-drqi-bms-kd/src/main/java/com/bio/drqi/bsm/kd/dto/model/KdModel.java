package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.List;

@Data
public abstract class KdModel {

    private FCreateOrgIdModel FCreateOrgId;

    private FUseOrgIdModel fUseOrgId;

    public KdModel build(String FNumber) {
        this.FCreateOrgId = new FCreateOrgIdModel(FNumber);
        this.fUseOrgId = new FUseOrgIdModel(FNumber);
        return this;
    }

    public abstract List<String> buildModifyFields();

}
