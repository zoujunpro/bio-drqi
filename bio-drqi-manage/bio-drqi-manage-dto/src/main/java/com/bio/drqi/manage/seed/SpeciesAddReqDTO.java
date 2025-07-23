package com.bio.drqi.manage.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SpeciesAddReqDTO {
    /**
     * 物种名称
     */
    @NotBlank(message = "物种名称必填")
    private String speciesName;

    /**
     * 物种编码
     */
    @NotBlank(message = "物种编码必填")
    private String speciesCode;

    /**
     * 种子编号前缀
     */
    @NotBlank(message = "种子编号前缀必填")
    private String numPrefix;

    @NotBlank(message = "拉丁文必填")
    private String latinName;

}
