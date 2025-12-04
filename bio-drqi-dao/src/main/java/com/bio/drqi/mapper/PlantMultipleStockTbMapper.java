package com.bio.drqi.mapper;
import java.util.Collection;
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

    PlantMultipleStockTb selectOneByRegionNumAndSeedNum(@Param("regionNum") String regionNum, @Param("seedNum") String seedNum);

    List<String> selectVectorTaskCodeBySourceCodeAndSpeciesCode(@Param("sourceCode") String sourceCode, @Param("speciesCode") String speciesCode);

    List<String> selectRegionNumBySourceCodeAndSpeciesCode(@Param("sourceCode") String sourceCode, @Param("speciesCode") String speciesCode);


    List<String> selectSpeciesCode();

    List<PlantMultipleStockTb> selectAllByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);

    PlantMultipleStockTb selectOneByVectorTaskCodeAndTransformCode(@Param("vectorTaskCode") String vectorTaskCode, @Param("transformCode") String transformCode);

    List<PlantMultipleStockTb>  selectSelective(PlantMultipleStockTb plantMultipleStockTb);

    int insertBatch(@Param("plantMultipleStockTbCollection") Collection<PlantMultipleStockTb> plantMultipleStockTbCollection);


    Long selectSumPlantNumber();

    Long selectSumSampleNumber();

    Long selectNoSampleNumber();

   Long selectCountBySourceCode(@Param("sourceCode") String sourceCode);
}




