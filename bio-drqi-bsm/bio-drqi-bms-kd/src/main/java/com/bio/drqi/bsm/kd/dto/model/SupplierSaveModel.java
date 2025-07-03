package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.List;

@Data
public class SupplierSaveModel extends KdModel {
    @Override
    public List<String> buildModifyFields() {
        return null;
    }
}
