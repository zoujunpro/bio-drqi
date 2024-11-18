package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BioTaskDtlTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bio_task_dtl_tb(项目任务表)】的数据库操作Mapper
* @createDate 2024-11-12 09:31:24
* @Entity com.bio.cer.domain.BioTaskDtlTb
*/
public interface BioTaskDtlTbMapper extends BaseMapper<BioTaskDtlTb> {

    BioTaskDtlTb selectOneByTaskNum(@Param("taskNum") String taskNum);

    BioTaskDtlTb selectOneByInstanceId(@Param("instanceId") Long instanceId);

    List<BioTaskDtlTb> selectForPendingApproval(@Param("applyUserId") String applyUserId,
                                                    @Param("taskNum") String taskNum,
                                                    @Param("taskTypeCode") String taskTypeCode,
                                                @Param("taskCategory") String taskCategory
                            );

    List<BioTaskDtlTb> selectForAlreadyApproval(@Param("applyUserId") String applyUserId,
                                                    @Param("taskNum") String taskNum,
                                                    @Param("taskTypeCode") String taskTypeCode,
                                                @Param("taskCategory") String taskCategory);

    List<BioTaskDtlTb> selectSelective(BioTaskDtlTb bioTaskDtlTb);


    List<BioTaskDtlTb> selectAllByTaskTypeCodeAndApplyUserIdOrderByIdDesc(@Param("taskTypeCode") String taskTypeCode, @Param("applyUserId") Integer applyUserId);


    Integer selectCountALl(@Param("taskCategory") String taskCategory);
    Integer selectCountMyApprove(@Param("applyUserId") Integer applyUserId,@Param("taskCategory") String taskCategory);


    Integer selectForPendingApprovalCount(@Param("applyUserId") String applyUserId,@Param("taskCategory") String taskCategory);

    Integer selectForAlreadyApprovalCount(@Param("applyUserId") String applyUserId,@Param("taskCategory") String taskCategory);


}




