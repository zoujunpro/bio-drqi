package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.PlantMultipleStockTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_multiple_stock_tb】的数据库操作Mapper
* @createDate 2025-11-21 09:32:00
* @Entity com.bio.drqi.domain.PlantMultipleStockTb
*/
public interface PlantMultipleStockTbMapper extends BaseMapper<PlantMultipleStockTb> {

    List<PlantMultipleStockTb> selectAllByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);

    List<PlantMultipleStockTb>  selectSelective(PlantMultipleStockTb plantMultipleStockTb);

}




