package com.bio.drqi.mapper;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.TcSampleTestBioInfoResultTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【tc_sample_test_bio_info_result_tb】的数据库操作Mapper
* @createDate 2025-05-21 09:49:06
* @Entity com.bio.drqi.domain.TcSampleTestBioInfoResultTb
*/
public interface TcSampleTestBioInfoResultTbMapper extends BaseMapper<TcSampleTestBioInfoResultTb> {

    int deleteByApplyNo(@Param("applyNo") String applyNo);

    int insertBatch(@Param("tcSampleTestBioInfoResultTbCollection") Collection<TcSampleTestBioInfoResultTb> tcSampleTestBioInfoResultTbCollection);

}




