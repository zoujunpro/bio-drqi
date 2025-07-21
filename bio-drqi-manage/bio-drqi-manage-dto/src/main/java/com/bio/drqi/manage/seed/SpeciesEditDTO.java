package com.bio.drqi.manage.seed;

import lombok.Data;

@Data
public class SpeciesEditDTO {
    private Integer id;

    /**
     * 物种名称
     */
    private String speciesName;

    /**
     * 物种编码
     */
    private String speciesCode;

    /**
     * 种子编号前缀
     */
    private String numPrefix;
}
