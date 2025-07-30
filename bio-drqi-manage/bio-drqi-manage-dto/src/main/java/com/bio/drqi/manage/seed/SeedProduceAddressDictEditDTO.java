package com.bio.drqi.manage.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SeedProduceAddressDictEditDTO {

    private Integer id;

    /**
     * 地点名称
     */
    @NotBlank(message = "地点名称缺失")
    private String addressName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;
}
