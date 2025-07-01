package com.bio.drqi.bsm.kd.dto.base;

import lombok.Data;

@Data
public class KdModel {

    private FCreateOrgIdModel FCreateOrgId;

    private FUseOrgIdModel fUseOrgId;

    public KdModel build(String FNumber) {
        this.FCreateOrgId = new FCreateOrgIdModel(FNumber);
        this.fUseOrgId = new FUseOrgIdModel(FNumber);
        return this;
    }


}
