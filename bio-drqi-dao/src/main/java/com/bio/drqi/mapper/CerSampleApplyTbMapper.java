package com.bio.drqi.mapper;

import com.bio.drqi.domain.CerSampleApplyTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【cer_sample_apply_tb(取样检测申请表)】的数据库操作Mapper
* @createDate 2025-10-22 09:58:22
* @Entity com.bio.drqi.domain.CerSampleApplyTb
*/
public interface CerSampleApplyTbMapper extends BaseMapper<CerSampleApplyTb> {


    List<CerSampleApplyTb>  selectSelective(CerSampleApplyTb cerSampleApplyTb);

    CerSampleApplyTb selectOneByApplyNo(@Param("applyNo") String applyNo);


    List<CerSampleApplyTb> selectAllByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);


    int deleteByApplyNo(@Param("applyNo") String applyNo);
}




