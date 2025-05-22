package com.bio.drqi.mapper;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.TcSampleTestBioResultRef;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【tc_sample_test_bio_result_ref】的数据库操作Mapper
* @createDate 2025-05-21 09:49:06
* @Entity com.bio.drqi.domain.TcSampleTestBioResultRef
*/
public interface TcSampleTestBioResultRefMapper extends BaseMapper<TcSampleTestBioResultRef> {

    int deleteByApplyNo(@Param("applyNo") String applyNo);

    int insertBatch(@Param("tcSampleTestBioResultRefCollection") Collection<TcSampleTestBioResultRef> tcSampleTestBioResultRefCollection);

    TcSampleTestBioResultRef selectOneByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);

}




