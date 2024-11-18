package com.bio.drqi.mapper;
import java.util.List;

import com.bio.drqi.domain.CerInstantVerifyTaskTb;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_instant_verify_task_tb(瞬时验证任务)】的数据库操作Mapper
* @createDate 2024-11-05 16:06:11
* @Entity com.bio.cer.domain.CerInstantVerifyTaskTb
*/
public interface CerInstantVerifyTaskTbMapper extends BaseMapper<CerInstantVerifyTaskTb> {

    CerInstantVerifyTaskTb selectOneByInstantVerifyCode(@Param("instantVerifyCode") String instantVerifyCode);

    int deleteByInstantVerifyCode(@Param("instantVerifyCode") String instantVerifyCode);

    List<CerInstantVerifyTaskTb> selectAllByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);

}




