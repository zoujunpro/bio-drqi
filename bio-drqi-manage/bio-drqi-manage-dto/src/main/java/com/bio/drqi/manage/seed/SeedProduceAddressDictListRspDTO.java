package com.bio.drqi.manage.seed;

import lombok.Data;

@Data
public class SeedProduceAddressDictListRspDTO {

    private Integer id;

    /**
     * 地点名称
     */
    private String addressName;


    /**
     * 地点名称
     */
    private String addressCode;


    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

}
