package com.bio.drqi.manage.seed;

import com.bio.common.core.util.EnumValue;
import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import javax.validation.Valid;

@Data
public class SeedStockPageReqDTO extends PageDTO {
    /**
     * 种子编号
     */
    private String seedNum;
    /**
     * 品种
     */
    private String breedCode;
    /**
     * 作物
     */
    private String species;
    /**
     * 库存位置
     */
    private String stockLocationNum;
    /**
     * 项目编号
     */
    private String vectorTaskCode;
    /**
     * 生产地点
     */
    private String productionLocationCode;
    /**
     * 收获方式，单珠和混珠
     */
    private String harvestType;
    /**
     * 来源地区
     */
    private String sourceAddress;
    /**
     * 代数
     */
    private String generation;
    /**
     * 来源
     */
    private String sourceType;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 授粉方式
     */
    private String pollinationMethod;

    /**
     * 上代种子编号
     */
    private String parentNum;

    /**
     * 种子类型
     */
    private String seedType;

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
     * 开始收获时间
     */
    private String beninHarvestTime;

    /**
     * 结束收获时间
     */
    private String endHarvestTime;

    private Order order;


    /**
     * 基因性性状
     */
    private String targetCharacter;

    /**
     * 别名
     */
    private String aliasName;

    /**
     * 基因型
     */
    private String geneType;

    private String materialType;


    private String projectCode;


    /**
     * Y代表非零库存
     */
    private String filterNullFlag;

    /**
     * 母本种子编号
     */
    private String matherSeedNum;


    private String matherSingleNum;


    private String pdNum;

    /**
     * 备注
     */
    private String remarks;

    @Data
    @Valid
    public static  class  Order{

        private String fieldName;

        /**
         * 正序 asc    倒叙 desc
         */
        @EnumValue(strValues = {"asc","desc"},message = "排序参数异常")
        private String orderType;
    }

}
