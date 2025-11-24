package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BioSampleApplyTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bio_sample_apply_tb(取样检测申请表)】的数据库操作Mapper
* @createDate 2025-11-19 11:08:14
* @Entity com.bio.drqi.domain.PlantSampleApplyTb
*/
public interface BioSampleApplyTbMapper extends BaseMapper<BioSampleApplyTb> {
    BioSampleApplyTb selectOneByApplyNo(@Param("applyNo") String applyNo);

    int deleteByApplyNo(@Param("applyNo") String applyNo);
}




