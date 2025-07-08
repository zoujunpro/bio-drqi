package com.bio.drqi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsStockLocationDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【bms_stock_location_dict(库位字典表)】的数据库操作Mapper
 * @createDate 2025-03-17 15:14:29
 * @Entity com.bio.drqi.domain.BmsStockLocationDict
 */
public interface BmsStockLocationDictMapper extends BaseMapper<BmsStockLocationDict> {
    List<BmsStockLocationDict> selectAllByUnitCode(@Param("unitCode") String unitCode);

    List<BmsStockLocationDict> selectSelective(BmsStockLocationDict bmsStockLocationDict);

    List<BmsStockLocationDict> selectAllByStockName(@Param("stockName") String stockName);

    BmsStockLocationDict selectOneByStockCodeAndLocationNumber(@Param("stockCode") String stockCode, @Param("locationNumber") String locationNumber);

    BmsStockLocationDict selectOneByUnitCodeAndLocationNumber(@Param("unitCode") String unitCode, @Param("locationNumber") String locationNumber);

}




