package com.bio.drqi.mapper;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerTransformTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_transform_tb(转化表)】的数据库操作Mapper
* @createDate 2023-11-14 14:31:17
* @Entity com.bio.cer.domain.CerTransformTb
*/
public interface CerTransformTbMapper extends BaseMapper<CerTransformTb> {
    int deleteByTaskNum(@Param("taskNum") String taskNum);

    int updateTaskStatusByTaskNum(@Param("taskStatus") String taskStatus, @Param("taskNum") String taskNum);

    List<CerTransformTb> selectAllByProjectId(@Param("projectId") Integer projectId);

    List<CerTransformTb> selectAllByVectorTaskIdAndPlasmidName(@Param("vectorTaskId") Integer vectorTaskId, @Param("plasmidName") String plasmidName);

    List<CerTransformTb> selectAllByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);

    List<CerTransformTb> selectAllByProjectIdAndTaskStatus(@Param("projectId") Integer projectId, @Param("taskStatus") String taskStatus);

    List<CerTransformTb> selectAllByVectorTaskIdAndTaskStatus(@Param("vectorTaskId") Integer vectorTaskId, @Param("taskStatus") String taskStatus);

    CerTransformTb selectOneByTransformCodeAndVectorTaskCode(@Param("transformCode") String transformCode, @Param("vectorTaskCode") String vectorTaskCode);

    List<CerTransformTb> selectAllByVectorTaskCodeAndPlasmidNameOrderByIdDesc(@Param("vectorTaskCode") String vectorTaskCode, @Param("plasmidName") String plasmidName);

    List<CerTransformTb> selectAllBySpeciesCodeAndVectorTaskTypeAndCreateTime(@Param("speciesCode") String speciesCode, @Param("vectorTaskType") String vectorTaskType, @Param("createTime") String createTime);

    List<CerTransformTb> selectAllBySpeciesCodeAndCreateTime(@Param("speciesCode") String speciesCode, @Param("createTime") String createTime);
   Integer selectSumInfectNumber();


}




