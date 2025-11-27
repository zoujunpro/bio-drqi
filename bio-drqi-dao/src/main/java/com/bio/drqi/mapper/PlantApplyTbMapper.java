package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.PlantApplyTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_apply_tb】的数据库操作Mapper
* @createDate 2025-11-14 13:37:00
* @Entity com.bio.drqi.domain.PlantExperimentTb
*/
public interface PlantApplyTbMapper extends BaseMapper<PlantApplyTb> {

    List<PlantApplyTb> selectSelective(PlantApplyTb plantApplyTb);

    PlantApplyTb selectOneByPlantApplyNum(@Param("plantApplyNum") String plantApplyNum);

    String selectMaxSampleCodePrefix();
}




