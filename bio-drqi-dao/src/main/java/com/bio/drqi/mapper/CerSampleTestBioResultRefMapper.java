package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSampleTestBioResultRef;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sample_test_bio_result_ref】的数据库操作Mapper
* @createDate 2024-12-24 15:13:05
* @Entity com.bio.drqi.domain.CerSampleTestBioResultRef
*/
public interface CerSampleTestBioResultRefMapper extends BaseMapper<CerSampleTestBioResultRef> {

    int deleteByApplyNo(@Param("applyNo") String applyNo);

    CerSampleTestBioResultRef selectOneBySampleCodeAndApplyNo(@Param("sampleCode") String sampleCode, @Param("applyNo") String applyNo);

}




