package com.bio.drqi.bsm.kd.dto.base;

import lombok.Data;

@Data
public class FUseOrgIdModel {
    private String FNumber;

    public FUseOrgIdModel(String FNumber) {
        this.FNumber = FNumber;
    }
}
