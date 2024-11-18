package com.bio.drqi.mapper;

import java.util.List;

import com.bio.drqi.domain.CerProjectTaskApproveTb;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【cer_project_task_approve_tb】的数据库操作Mapper
 * @createDate 2023-11-09 11:43:54
 * @Entity com.bio.cer.domain.CerProjectTaskApproveTb
 */
public interface CerProjectTaskApproveTbMapper extends BaseMapper<CerProjectTaskApproveTb> {
    List<CerProjectTaskApproveTb> selectAllByTaskId(@Param("taskId") Integer taskId);

    CerProjectTaskApproveTb selectOneByTaskIdAndApproveUserId(@Param("taskId") Integer taskId, @Param("approveUserId") Integer approveUserId);

    int deleteByTaskId(@Param("taskId") Integer taskId);

    int updateNullById(@Param("id") Integer id);
}




