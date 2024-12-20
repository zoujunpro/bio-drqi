package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSampleLayoutTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sample_layout_tb】的数据库操作Mapper
* @createDate 2024-10-11 14:23:40
* @Entity com.bio.cer.domain.CerSampleLayoutTb
*/
public interface CerSampleLayoutTbMapper extends BaseMapper<CerSampleLayoutTb> {

    CerSampleLayoutTb selectOneByApplyNo(@Param("applyNo") String applyNo);

    int deleteByApplyNo(@Param("applyNo") String applyNo);

}




