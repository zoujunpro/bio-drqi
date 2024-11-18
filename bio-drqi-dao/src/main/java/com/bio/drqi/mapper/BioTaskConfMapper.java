package com.bio.drqi.mapper;

import com.bio.drqi.domain.BioTaskConf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bio_task_conf(任务配置信息)】的数据库操作Mapper
* @createDate 2024-11-12 09:37:04
* @Entity com.bio.cer.domain.BioTaskConf
*/
public interface BioTaskConfMapper extends BaseMapper<BioTaskConf> {
    BioTaskConf selectOneByTaskTypeCode(@Param("taskTypeCode") String taskTypeCode);
    BioTaskConf selectOneByProcessId(@Param("processId") Long processId);

    List<BioTaskConf> selectAllByTaskCategory(@Param("taskCategory") String taskCategory);

    List<BioTaskConf> selectAllOrderByIdDesc();
}




