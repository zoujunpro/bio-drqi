package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.List;

/**
 * 出库
 */
@Data
public class OutStockSaveModel extends KdModel {


    @Override
    public List<String> buildModifyFields() {
        return null;
    }
}
