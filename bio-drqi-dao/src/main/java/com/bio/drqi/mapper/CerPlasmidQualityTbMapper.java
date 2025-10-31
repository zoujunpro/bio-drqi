package com.bio.drqi.mapper;
import java.util.List;

import com.bio.drqi.domain.CerPlasmidQualityTb;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_plasmid_quality_tb(质粒质检表)】的数据库操作Mapper
* @createDate 2023-11-14 10:01:10
* @Entity com.bio.cer.domain.CerPlasmidQualityTb
*/
public interface CerPlasmidQualityTbMapper extends BaseMapper<CerPlasmidQualityTb> {

    List<CerPlasmidQualityTb>  selectSelective(CerPlasmidQualityTb cerPlasmidQualityTb);

    List<CerPlasmidQualityTb> selectAllByVectorIdAndQualityInspectionResult(@Param("vectorId") Integer vectorId, @Param("qualityInspectionResult") String qualityInspectionResult);

    List<CerPlasmidQualityTb> selectAllByVectorTaskCodeAndPlasmidName(@Param("vectorTaskCode") String vectorTaskCode, @Param("plasmidName") String plasmidName);
    int deleteByTaskNum(@Param("taskNum") String taskNum);

    int updateTaskStatusByTaskNum(@Param("taskStatus") String taskStatus, @Param("taskNum") String taskNum);

    List<CerPlasmidQualityTb> selectAllByProjectId(@Param("projectId") Integer projectId);

    List<CerPlasmidQualityTb> selectAllByTaskNum(@Param("taskNum") String taskNum);

    List<CerPlasmidQualityTb> selectAllByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);

    int updateQualityInspectionResultByVectorTaskId(@Param("qualityInspectionResult") String qualityInspectionResult, @Param("vectorTaskId") Integer vectorTaskId);

}




