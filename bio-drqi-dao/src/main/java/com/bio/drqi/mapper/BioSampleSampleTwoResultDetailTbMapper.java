package com.bio.drqi.mapper;

import com.bio.drqi.domain.BioSampleSampleTwoResultDetailTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author zou'jun
 * @description 针对表【bio_sample_sample_two_result_detail_tb】的数据库操作Mapper
 * @createDate 2025-10-30 09:22:06
 * @Entity com.bio.drqi.domain.BioSampleSampleTwoResultDetailTb
 */
public interface BioSampleSampleTwoResultDetailTbMapper extends BaseMapper<BioSampleSampleTwoResultDetailTb> {
    int deleteByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);

    List<BioSampleSampleTwoResultDetailTb> selectAllByIdIn(@Param("idList") Collection<Integer> idList);


    List<BioSampleSampleTwoResultDetailTb> selectAllByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);


    int insertBatch(@Param("bioSampleSampleTwoResultDetailTbCollection") Collection<BioSampleSampleTwoResultDetailTb> bioSampleSampleTwoResultDetailTbCollection);

    List<BioSampleSampleTwoResultDetailTb> selectAllBySampleCodeIn(@Param("sampleCodeList") Collection<String> sampleCodeList);
}




