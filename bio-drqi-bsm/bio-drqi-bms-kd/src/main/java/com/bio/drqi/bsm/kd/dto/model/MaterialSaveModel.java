package com.bio.drqi.bsm.kd.dto.model;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 物料
 */
@Data
public class MaterialSaveModel extends KdModel {
    /**
     * 实体主键
     */
    private Integer FMATERIALID=0;

    /**
     * 编码
     */
    private String Fnumber;

    /**
     * 名称
     */
    private String Fname;

    public static void main(String[] args) {
        MaterialSaveModel materialSaveModel=new MaterialSaveModel();
        materialSaveModel.setFMATERIALID(1);
        materialSaveModel.setFnumber("2");
        materialSaveModel.setFname("3");
        materialSaveModel.setFCreateOrgId(null);
        materialSaveModel.setFUseOrgId(null);
        test(materialSaveModel);

    }

    public static void test(KdModel model){
        System.out.println(JSONUtil.toJsonStr(model));
    }



}
