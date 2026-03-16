package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BioSampleApplyTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bio_sample_apply_tb(取样检测申请表)】的数据库操作Mapper
* @createDate 2025-11-26 10:24:53
* @Entity com.bio.drqi.domain.BioSampleApplyTb
*/
public interface BioSampleApplyTbMapper extends BaseMapper<BioSampleApplyTb> {

    BioSampleApplyTb selectOneByApplyNo(@Param("applyNo") String applyNo);

    int deleteByApplyNo(@Param("applyNo") String applyNo);

    List<BioSampleApplyTb> selectSelective(BioSampleApplyTb bioSampleApplyTb);

    List<BioSampleApplyTb> selectAllByVectorTaskCodeAndSourceCode(@Param("vectorTaskCode") String vectorTaskCode,@Param("sourceCode")String sourceCode);
}




