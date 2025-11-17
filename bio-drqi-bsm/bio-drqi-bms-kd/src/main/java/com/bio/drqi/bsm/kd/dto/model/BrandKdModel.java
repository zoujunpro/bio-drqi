package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BrandKdModel extends KdModel {
    private String FID;
    private String Fnumber;
    private String Fname;
    private String Fdescription;


    @Override
    public List<String> buildModifyFields() {
        List<String> list = new ArrayList<>();
        list.add("Fnumber");
        list.add("Fname");
        return list;
    }
}
