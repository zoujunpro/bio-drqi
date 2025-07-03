package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FixValueModel extends KdModel {
    private Integer FStockId;
    private String Fnumber;
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
