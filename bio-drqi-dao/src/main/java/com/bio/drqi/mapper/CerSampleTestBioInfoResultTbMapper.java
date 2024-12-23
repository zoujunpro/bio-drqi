package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSampleTestBioInfoResultTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sample_test_bio_info_result_tb】的数据库操作Mapper
* @createDate 2024-12-23 17:11:30
* @Entity com.bio.drqi.domain.CerSampleTestBioInfoResultTb
*/
public interface CerSampleTestBioInfoResultTbMapper extends BaseMapper<CerSampleTestBioInfoResultTb> {

    CerSampleTestBioInfoResultTb selectOneBySampleIdAndUniqueDbCode(@Param("sampleId") String sampleId, @Param("uniqueDbCode") String uniqueDbCode);

}




