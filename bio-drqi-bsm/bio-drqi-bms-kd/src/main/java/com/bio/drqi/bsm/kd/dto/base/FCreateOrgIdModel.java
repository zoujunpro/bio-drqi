package com.bio.drqi.bsm.kd.dto.base;

import lombok.Data;

@Data
public class FCreateOrgIdModel {
    private String FNumber;

    public FCreateOrgIdModel(String FNumber) {
        this.FNumber = FNumber;
    }
}
