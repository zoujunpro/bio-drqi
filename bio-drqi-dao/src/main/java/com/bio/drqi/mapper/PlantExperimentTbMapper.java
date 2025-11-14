package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.PlantExperimentTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_experiment_tb】的数据库操作Mapper
* @createDate 2025-11-14 13:37:00
* @Entity com.bio.drqi.domain.PlantExperimentTb
*/
public interface PlantExperimentTbMapper extends BaseMapper<PlantExperimentTb> {



    List<PlantExperimentTb> selectSelective(PlantExperimentTb plantExperimentTb);
}




