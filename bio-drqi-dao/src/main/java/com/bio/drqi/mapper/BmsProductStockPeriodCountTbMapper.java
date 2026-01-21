package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsProductStockPeriodCountTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bms_product_stock_period_count_tb】的数据库操作Mapper
* @createDate 2026-01-09 09:34:42
* @Entity com.bio.drqi.domain.BmsProductStockPeriodCountTb
*/
public interface BmsProductStockPeriodCountTbMapper extends BaseMapper<BmsProductStockPeriodCountTb> {

    List<BmsProductStockPeriodCountTb> selectSelective(BmsProductStockPeriodCountTb bmsProductStockPeriodCountTb);

    List<BmsProductStockPeriodCountTb> selectAllByPeriodTime(@Param("periodTime") String periodTime);

    BmsProductStockPeriodCountTb selectOneByProductInnerCodeAndUnitCodeAndStockCodeAndBatchNoAndPeriodTime(@Param("productInnerCode") String productInnerCode, @Param("unitCode") String unitCode, @Param("stockCode") String stockCode, @Param("batchNo") String batchNo, @Param("periodTime") String periodTime);
}




