package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSubProjectTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sub_project_tb】的数据库操作Mapper
* @createDate 2023-11-13 18:13:15
* @Entity com.bio.cer.domain.CerSubProjectTb
*/
public interface CerSubProjectTbMapper extends BaseMapper<CerSubProjectTb> {

    List<CerSubProjectTb> selectSelective(CerSubProjectTb cerSubProjectTb);
    List<CerSubProjectTb> selectAllByTaskNum(@Param("taskNum") String taskNum);

    List<CerSubProjectTb> selectAllByProjectId(@Param("projectId") Integer projectId);

    int deleteByTaskNum(@Param("taskNum") String taskNum);

    int updateTaskStatusByTaskNum(@Param("taskStatus") String taskStatus, @Param("taskNum") String taskNum);


    CerSubProjectTb selectOneBySubProjectCode(@Param("subProjectCode") String subProjectCode);
}




