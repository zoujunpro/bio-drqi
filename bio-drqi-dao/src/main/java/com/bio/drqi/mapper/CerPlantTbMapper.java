package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.CerPlantTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_plant_tb(cer种植表)】的数据库操作Mapper
* @createDate 2023-11-20 10:09:46
* @Entity com.bio.cer.domain.CerPlantTb
*/
public interface CerPlantTbMapper extends BaseMapper<CerPlantTb> {

    int deleteByProjectIdAndSampleCodeIn(@Param("projectId") Integer projectId, @Param("sampleCodeList") Collection<String> sampleCodeList);

    int insertBatch(@Param("cerPlantTbCollection") Collection<CerPlantTb> cerPlantTbCollection);

    int deleteByTaskNum(@Param("taskNum") String taskNum);

    List<CerPlantTb> selectAllByProjectId(@Param("projectId") Integer projectId);

    int updateTaskStatusByTaskNum(@Param("taskStatus") String taskStatus, @Param("taskNum") String taskNum);

}




