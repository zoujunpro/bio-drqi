package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

/**
 * 物料
 */
@Data
public class ProjectSaveModel extends KdModel {
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



}
