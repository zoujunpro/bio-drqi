package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerPlantDtlTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_plant_dtl_tb(cer种植表)】的数据库操作Mapper
* @createDate 2024-11-21 10:36:41
* @Entity com.bio.drqi.domain.CerPlantDtlTb
*/
public interface CerPlantDtlTbMapper extends BaseMapper<CerPlantDtlTb> {
    CerPlantDtlTb selectOneByUniqueCode(@Param("uniqueCode") String uniqueCode);
}




