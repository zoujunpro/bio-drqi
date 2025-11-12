package com.bio.drqi.mapper;

import com.bio.drqi.domain.BioSampleSampleTwoResultTb;
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
public interface BioSampleSampleTwoResultTbMapper extends BaseMapper<BioSampleSampleTwoResultTb> {
    int deleteByApplyNo(@Param("applyNo") String applyNo);

    int deleteByUploadNum(@Param("uploadNum") String uploadNum);

    List<BioSampleSampleTwoResultTb> selectAllByUploadNum(@Param("uploadNum") String uploadNum);

    List<BioSampleSampleTwoResultTb> selectSelective(BioSampleSampleTwoResultTb bioSampleSampleTwoResultTb);

    int insertBatch(@Param("bioSampleSampleTwoResultTbCollection") Collection<BioSampleSampleTwoResultTb> bioSampleSampleTwoResultTbCollection);
    List<BioSampleSampleTwoResultTb> selectAllByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);
}




