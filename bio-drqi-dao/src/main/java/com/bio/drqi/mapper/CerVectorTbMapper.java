package com.bio.drqi.mapper;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerVectorTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_vector_tb(载体信息主表)】的数据库操作Mapper
* @createDate 2023-11-13 14:02:22
* @Entity com.bio.cer.domain.CerVectorTb
*/
public interface CerVectorTbMapper extends BaseMapper<CerVectorTb> {

    int deleteByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);

    List<CerVectorTb> selectSelective(CerVectorTb cerVectorTb);
    List<CerVectorTb> selectAllByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);

    CerVectorTb selectOneByPlasmidNameAndVectorTaskId(@Param("plasmidName") String plasmidName, @Param("vectorTaskId") Integer vectorTaskId);

    int updateQualityInspectionResultByIdIn(@Param("qualityInspectionResult") String qualityInspectionResult, @Param("idList") Collection<Integer> idList);

    int insertBatch(@Param("cerVectorTbCollection") Collection<CerVectorTb> cerVectorTbCollection);

}




