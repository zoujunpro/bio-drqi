package com.bio.drqi.mapper;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSampleTestBioResultRef;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sample_test_bio_result_ref】的数据库操作Mapper
* @createDate 2024-12-25 15:48:14
* @Entity com.bio.drqi.domain.CerSampleTestBioResultRef
*/
public interface CerSampleTestBioResultRefMapper extends BaseMapper<CerSampleTestBioResultRef> {
    int deleteByApplyNo(@Param("applyNo") String applyNo);


    int insertBatch(@Param("cerSampleTestBioResultRefCollection") Collection<CerSampleTestBioResultRef> cerSampleTestBioResultRefCollection);

    CerSampleTestBioResultRef selectOneByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);
}




