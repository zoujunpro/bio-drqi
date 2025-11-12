package com.bio.drqi.mapper;

import com.bio.drqi.domain.BioSampleTestTwoResultTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
* @author zou'jun
* @description 针对表【bio_sample_sample_two_result_tb】的数据库操作Mapper
* @createDate 2025-10-30 09:22:06
* @Entity com.bio.drqi.domain.BioSampleSampleTwoResultTb
*/
public interface BioSampleTestTwoResultTbMapper extends BaseMapper<BioSampleTestTwoResultTb> {
    int deleteByApplyNo(@Param("applyNo") String applyNo);

    int deleteByUploadNum(@Param("uploadNum") String uploadNum);

    List<BioSampleTestTwoResultTb> selectAllByUploadNum(@Param("uploadNum") String uploadNum);

    List<BioSampleTestTwoResultTb> selectSelective(BioSampleTestTwoResultTb bioSampleSampleTwoResultTb);

    int insertBatch(@Param("bioSampleSampleTwoResultTbCollection") Collection<BioSampleTestTwoResultTb> bioSampleSampleTwoResultTbCollection);
    List<BioSampleTestTwoResultTb> selectAllByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);
}




