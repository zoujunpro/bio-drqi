package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;
import java.util.List;

import com.bio.drqi.domain.PlantApplyDetailTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_experiment_detail_tb】的数据库操作Mapper
* @createDate 2025-11-14 16:14:15
* @Entity com.bio.drqi.domain.PlantExperimentDetailTb
*/
public interface PlantApplyDetailTbMapper extends BaseMapper<PlantApplyDetailTb> {
    int insertBatch(@Param("plantExperimentDetailTbCollection") Collection<PlantApplyDetailTb> plantExperimentDetailTbCollection);

    List<PlantApplyDetailTb> selectSelective(PlantApplyDetailTb plantExperimentDetailTb);

    List<PlantApplyDetailTb> selectAllByRegionNum(@Param("regionNum") String regionNum);

    PlantApplyDetailTb selectOneByRegionNumAndSeedNum(@Param("regionNum") String regionNum, @Param("seedNum") String seedNum);

    List<PlantApplyDetailTb> selectAllByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);


}




