package com.bio.drqi.bsm.kd.dto.model;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.List;

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



    @Override
    public List<String> buildModifyFields() {
        return null;
    }
}
