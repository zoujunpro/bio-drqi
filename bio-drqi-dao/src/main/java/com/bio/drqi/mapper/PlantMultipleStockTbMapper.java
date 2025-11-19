package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.PlantMultipleStockTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_multiple_stock_tb】的数据库操作Mapper
* @createDate 2025-11-17 16:48:24
* @Entity com.bio.drqi.domain.PlantMultipleStockTb
*/
public interface PlantMultipleStockTbMapper extends BaseMapper<PlantMultipleStockTb> {
    int insertBatch(@Param("plantMultipleStockTbCollection") Collection<PlantMultipleStockTb> plantMultipleStockTbCollection);

    PlantMultipleStockTb selectOneByVectorTaskCodeAndTransformCode(@Param("vectorTaskCode") String vectorTaskCode, @Param("transformCode") String transformCode);

    PlantMultipleStockTb selectOneByRegionNumAndSeedNum(@Param("regionNum") String regionNum, @Param("seedNum") String seedNum);
}




