package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.CerVectorGroupTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_vector_group_tb】的数据库操作Mapper
* @createDate 2023-12-07 17:11:38
* @Entity com.bio.cer.domain.CerVectorGroupTb
*/
public interface CerVectorGroupTbMapper extends BaseMapper<CerVectorGroupTb> {
    int insertBatch(@Param("cerVectorGroupTbCollection") Collection<CerVectorGroupTb> cerVectorGroupTbCollection);

    List<CerVectorGroupTb> selectAllByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);

    CerVectorGroupTb selectOneByGroupNameAndVectorTaskId(@Param("groupName") String groupName, @Param("vectorTaskId") Integer vectorTaskId);

    int deleteByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);

    int updateQualityInspectionResultByVectorTaskIdAndQualityInspectionResultIsNull(@Param("qualityInspectionResult") String qualityInspectionResult, @Param("vectorTaskId") Integer vectorTaskId);

}




