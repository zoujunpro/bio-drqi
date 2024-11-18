package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSampleApplyTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sample_apply_tb(取样检测申请表)】的数据库操作Mapper
* @createDate 2024-08-28 14:47:51
* @Entity com.bio.cer.domain.CerSampleApplyTb
*/
public interface CerSampleApplyTbMapper extends BaseMapper<CerSampleApplyTb> {

    CerSampleApplyTb selectOneByApplyNo(@Param("applyNo") String applyNo);

    List<CerSampleApplyTb> selectSelective(CerSampleApplyTb cerSampleApplyTb);

    List<CerSampleApplyTb> selectAllByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);


    List<CerSampleApplyTb> selectAllByCurrentStepCode(@Param("currentStepCode") String currentStepCode);

    int deleteByApplyNo(@Param("applyNo") String applyNo);



}




