package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.PlantSingleStockTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_single_stock_tb(cer种植表)】的数据库操作Mapper
* @createDate 2025-11-17 16:47:48
* @Entity com.bio.drqi.domain.PlantSingleStockTb
*/
public interface PlantSingleStockTbMapper extends BaseMapper<PlantSingleStockTb> {

    PlantSingleStockTb selectOneByPlantCode(@Param("plantCode") String plantCode);

}




