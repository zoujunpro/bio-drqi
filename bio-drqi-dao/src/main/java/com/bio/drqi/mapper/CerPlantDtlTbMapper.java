package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerPlantDtlTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【cer_plant_dtl_tb(cer种植表)】的数据库操作Mapper
* @createDate 2024-12-05 13:53:21
* @Entity com.bio.drqi.domain.CerPlantDtlTb
*/
public interface CerPlantDtlTbMapper extends BaseMapper<CerPlantDtlTb> {

    List<CerPlantDtlTb> selectSelective(CerPlantDtlTb cerPlantDtlTb);

    CerPlantDtlTb selectOneByPlantCodeAndVectorTaskCode(@Param("plantCode") String plantCode, @Param("vectorTaskCode") String vectorTaskCode);

}




