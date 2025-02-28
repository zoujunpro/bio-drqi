package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.BigDecimalSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeedBaseInfoRspDTO {

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 项目号
     */
    private String projectCode;

    /**
     * 代次
     */
    private String generation;

    /**
     * 项目物种名称
     */
    private String speciesName;
    /**
     * 品种名称
     */
    private String breedName;
    /**
     * 种子类型
     */
    private String seedType;

    /**
     * 收获方式，单珠和混珠
     */
    private String harvestType;

    /**
     * 种子数量
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal seedNumber;

    /**
     * 计量单位g/kg/粒
     */
    private String unit;

    /**
     * 种子来源和地址
     */
    private String sourceAndLocation;

    /**
     * 库位编号
     */
    private String stockLocationNum;


}
