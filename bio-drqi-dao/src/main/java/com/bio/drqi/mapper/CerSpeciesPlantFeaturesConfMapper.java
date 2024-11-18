package com.bio.drqi.mapper;
import java.util.List;

import com.bio.drqi.domain.CerSpeciesPlantFeaturesConf;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_species_plant_features_conf(不同物种种植特性信息配置表)】的数据库操作Mapper
* @createDate 2023-11-17 15:37:24
* @Entity com.bio.cer.domain.CerSpeciesPlantFeaturesConf
*/
public interface CerSpeciesPlantFeaturesConfMapper extends BaseMapper<CerSpeciesPlantFeaturesConf> {

    List<CerSpeciesPlantFeaturesConf> selectAllBySpeciesCodeOrderByOrderNum(@Param("speciesCode") String speciesCode);

}




