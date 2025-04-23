package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerVectorStepLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_vector_step_log】的数据库操作Mapper
* @createDate 2024-09-06 16:10:05
* @Entity com.bio.cer.domain.CerVectorStepLog
*/
public interface CerVectorStepLogMapper extends BaseMapper<CerVectorStepLog> {
    CerVectorStepLog selectOneByVectorTaskIdAndStepCode(@Param("vectorTaskId") Integer vectorTaskId, @Param("stepCode") String stepCode);

    List<CerVectorStepLog> selectAllByVectorTaskIdOrderById(@Param("vectorTaskId") Integer vectorTaskId);

    int deleteByVectorTaskIdAndStepCodeAndTaskNum(@Param("vectorTaskId") Integer vectorTaskId, @Param("stepCode") String stepCode, @Param("taskNum") String taskNum);

    int deleteByTaskNumAndStepCode(@Param("taskNum") String taskNum, @Param("stepCode") String stepCode);
}




