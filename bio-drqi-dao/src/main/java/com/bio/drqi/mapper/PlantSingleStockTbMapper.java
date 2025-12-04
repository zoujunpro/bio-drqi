package com.bio.drqi.mapper;

import java.util.Collection;

import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.PlantSingleStockTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author zou'jun
 * @description 针对表【plant_single_stock_tb(cer种植表)】的数据库操作Mapper
 * @createDate 2025-11-21 09:02:36
 * @Entity com.bio.drqi.domain.PlantSingleStockTb
 */
public interface PlantSingleStockTbMapper extends BaseMapper<PlantSingleStockTb> {
    List<String> selectSpeciesCode();

    List<PlantSingleStockTb> selectAllByPlantCodeIn(@Param("plantCodeList") Collection<String> plantCodeList);

    PlantSingleStockTb selectOneByPlantCode(@Param("plantCode") String plantCode);

    List<PlantSingleStockTb> selectSelective(PlantSingleStockTb plantSingleStockTb);

    int insertBatch(@Param("plantSingleStockTbCollection") Collection<PlantSingleStockTb> plantSingleStockTbCollection);

    int deleteByTaskNum(@Param("taskNum") String taskNum);

    List<PlantSingleStockTb> selectCountGroupByPlantStatus(@Param("vectorTaskCode") String vectorTaskCode, @Param("sourceCode") String sourceCode);

    List<PlantSingleStockTb> selectCountByVectorTaskCode();


}




