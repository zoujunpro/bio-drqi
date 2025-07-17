package com.bio.drqi.mapper;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsProductStockTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bms_product_stock_tb】的数据库操作Mapper
* @createDate 2025-03-17 15:14:29
* @Entity com.bio.drqi.domain.BmsProductStockTb
*/
public interface BmsProductStockTbMapper extends BaseMapper<BmsProductStockTb> {
    List<BmsProductStockTb> selectSelective(BmsProductStockTb bmsProductStockTb);

    BmsProductStockTb selectOneByUniqueCode(@Param("uniqueCode") String uniqueCode);
    List<BmsProductStockTb> selectAllByProductInnerCodeAndUnitCodeAndBatchNo(@Param("productInnerCode") String productInnerCode, @Param("unitCode") String unitCode, @Param("batchNo") String batchNo);

    BmsProductStockTb selectOneByProductInnerCodeAndUnitCodeAndBatchNoAndStockCode(@Param("productInnerCode") String productInnerCode, @Param("unitCode") String unitCode, @Param("batchNo") String batchNo, @Param("stockCode") String stockCode);
    List<String> selectProductNameByUnitCode(@Param("unitCode") String unitCode);

    List<BmsProductStockTb> selectAllByStockCode(@Param("stockCode") String stockCode);

    List<BmsProductStockTb> selectAllByStockLocationNumberIn(@Param("stockLocationNumberList") Collection<String> stockLocationNumberList);


}




