package com.bio.drqi.mapper;
import java.util.Collection;
import java.util.List;

import com.bio.drqi.domain.CerVectorTaskPlanLog;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_vector_task_plan_log(实施方案时间计划表)】的数据库操作Mapper
* @createDate 2024-10-25 09:26:05
* @Entity com.bio.cer.domain.CerVectorTaskPlanLog
*/
public interface CerVectorTaskPlanLogMapper extends BaseMapper<CerVectorTaskPlanLog> {

    List<CerVectorTaskPlanLog> selectAllByVectorTaskIdOrderByIdAsc(@Param("vectorTaskId") Integer vectorTaskId);

    CerVectorTaskPlanLog selectOneByVectorTaskIdAndEventType(@Param("vectorTaskId") Integer vectorTaskId, @Param("eventType") String eventType);

    List<CerVectorTaskPlanLog> selectAllByVectorTaskIdIn(@Param("vectorTaskIdList") Collection<Integer> vectorTaskIdList);

    List<CerVectorTaskPlanLog> selectAll();

}




