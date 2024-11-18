package com.bio.cer.seed;

import com.bio.cer.base.PageDTO;
import lombok.Data;

@Data
public class SeedBaseInfoReqDTO  extends PageDTO {

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
    private String species;
    /**
     * 品种名称
     */
    private String breedCode;
    /**
     * 种子类型
     */
    private String seedType;

    /**
     * 收获方式，单珠和混珠
     */
    private String harvestType;

    private String productionLocationName;


    /**
     * 授粉方式
     */
    private String pollinationMethod;

    /**
     * 上代种子编号
     */
    private String parentNum;

    /**
     * yyyyMMdd
     * 检索开始时间
     */
    private String beginDate;
    /**
     * yyyyMMdd
     * 检索结束时间
     */
    private String endDate;
    /**
     * 收获时间
     */
    private String harvestTime;


    /**
     * 种植编号
     */
    private String plantNum;

    private String sourceType;



}
