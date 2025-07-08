package com.bio.drqi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsStockDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【bms_stock_dict】的数据库操作Mapper
 * @createDate 2025-07-08 09:23:23
 * @Entity com.bio.drqi.domain.BmsStockDict
 */
public interface BmsStockDictMapper extends BaseMapper<BmsStockDict> {

    BmsStockDict selectOneByStockName(@Param("stockName") String stockName);

    BmsStockDict selectOneByStockCode(@Param("stockCode") String stockCode);

    BmsStockDict selectOneByStockNameAndUnitCode(@Param("stockName") String stockName, @Param("unitCode") String unitCode);

    List<BmsStockDict> selectAllByUnitCodeOrderByIdDesc(@Param("unitCode") String unitCode);

}




