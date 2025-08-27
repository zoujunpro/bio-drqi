package com.bio.drqi.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerVectorTaskTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【cer_vector_task_tb(载体构建任务)】的数据库操作Mapper
 * @createDate 2023-11-13 18:21:50
 * @Entity com.bio.cer.domain.CerVectorTaskTb
 */
public interface CerVectorTaskTbMapper extends BaseMapper<CerVectorTaskTb> {
    CerVectorTaskTb selectOneByTaskNum(@Param("taskNum") String taskNum);

    int updateTaskStatusByTaskNum(@Param("taskStatus") String taskStatus, @Param("taskNum") String taskNum);

    List<CerVectorTaskTb> selectAllByProjectIdOrderById(@Param("projectId") Integer projectId);

    List<CerVectorTaskTb> selectAllBySubProjectId(@Param("subProjectId") Integer subProjectId);
    List<CerVectorTaskTb> listForVectorBuild(@Param("subProjectId") Integer subProjectId);


    CerVectorTaskTb selectOneByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);

    List<CerVectorTaskTb> selectAllByVectorTaskCodeIn(@Param("vectorTaskCodeList") Collection<String> vectorTaskCodeList);

    List<CerVectorTaskTb> listForTransForm(@Param("subProjectId") Integer subProjectId);

    List<CerVectorTaskTb> listForMoveSeed();

    List<CerVectorTaskTb> listForPlasmid(@Param("subProjectId") Integer subProjectId);

    Integer selectCountNum();

    List<CerVectorTaskTb> selectAllForBoard(@Param("userId") Integer userId, @Param("projectId") Integer projectId, @Param("speciesCode") String speciesCode, @Param("taskStatus") String taskStatus);

    List<String> selectAllSpeciesCode();

    List<CerVectorTaskTb> selectAllBySpeciesCode(@Param("speciesCode") String speciesCode);
}




