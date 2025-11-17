package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StockModel extends KdModel {
    private String FStockId;
    private String F_WAUJ_UUID;
    private String Fname;
    private String FDescription;

    private String FStockProperty;
    private String FStockStatusType;

    @Override
    public List<String> buildModifyFields() {
        List<String> list=new ArrayList<>();
        list.add("Fnumber");
        list.add("Fname");
        return list;
    }


}
