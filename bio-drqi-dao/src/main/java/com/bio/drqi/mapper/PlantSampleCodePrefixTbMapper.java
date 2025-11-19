package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.PlantSampleCodePrefixTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_sample_coode_prefix_tb】的数据库操作Mapper
* @createDate 2025-11-19 14:01:26
* @Entity com.bio.drqi.domain.PlantSampleCodePrefixTb
*/
public interface PlantSampleCodePrefixTbMapper extends BaseMapper<PlantSampleCodePrefixTb> {

    PlantSampleCodePrefixTb selectOneBySampleCodePrefix(@Param("sampleCodePrefix") String sampleCodePrefix);


}




