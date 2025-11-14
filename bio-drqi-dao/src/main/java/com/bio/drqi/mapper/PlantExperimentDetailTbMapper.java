package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.PlantExperimentDetailTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_experiment_detail_tb】的数据库操作Mapper
* @createDate 2025-11-14 16:14:15
* @Entity com.bio.drqi.domain.PlantExperimentDetailTb
*/
public interface PlantExperimentDetailTbMapper extends BaseMapper<PlantExperimentDetailTb> {
    int insertBatch(@Param("plantExperimentDetailTbCollection") Collection<PlantExperimentDetailTb> plantExperimentDetailTbCollection);
}




