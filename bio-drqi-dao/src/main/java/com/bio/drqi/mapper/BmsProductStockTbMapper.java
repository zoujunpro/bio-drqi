package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsProductStockTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bms_product_stock_tb】的数据库操作Mapper
* @createDate 2025-03-17 15:14:29
* @Entity com.bio.drqi.domain.BmsProductStockTb
*/
public interface BmsProductStockTbMapper extends BaseMapper<BmsProductStockTb> {

    BmsProductStockTb selectOneByBrandCodeAndProductSpecsAndProductNameAndBatchNoAndUnitCode(@Param("brandCode") String brandCode, @Param("productSpecs") String productSpecs, @Param("productName") String productName, @Param("batchNo") String batchNo, @Param("unitCode") String unitCode);

}




