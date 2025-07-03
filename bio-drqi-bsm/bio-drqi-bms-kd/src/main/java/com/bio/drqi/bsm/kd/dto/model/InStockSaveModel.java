package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.List;

/**
 * 物料
 */
@Data
public class InStockSaveModel extends KdModel {


    @Override
    public List<String> buildModifyFields() {
        return null;
    }
}
