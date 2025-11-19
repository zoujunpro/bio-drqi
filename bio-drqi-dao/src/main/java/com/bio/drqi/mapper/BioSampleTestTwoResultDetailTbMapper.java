package com.bio.drqi.mapper;

import com.bio.drqi.domain.BioSampleTestTwoResultDetailTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author zou'jun
 * @description 针对表【bio_sample_test_two_result_detail_tb】的数据库操作Mapper
 * @createDate 2025-10-30 09:22:06
 * @Entity com.bio.drqi.domain.BioSampleSampleTwoResultDetailTb
 */
public interface BioSampleTestTwoResultDetailTbMapper extends BaseMapper<BioSampleTestTwoResultDetailTb> {

    int deleteByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);
    int deleteByApplyNoAndSampleCodeAndUniqueDbCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode, @Param("uniqueDbCode") String uniqueDbCode);

    List<BioSampleTestTwoResultDetailTb> selectAllByIdIn(@Param("idList") Collection<Integer> idList);

    List<BioSampleTestTwoResultDetailTb> selectAllByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);

    List<BioSampleTestTwoResultDetailTb> selectAllByApplyNoAndSampleCodeAndConfirmStatus(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode, @Param("confirmStatus") String confirmStatus);
    List<BioSampleTestTwoResultDetailTb> selectAllBySampleIdAndRunId(@Param("sampleId") String sampleId, @Param("runId") String runId);

    int deleteByIdIn(@Param("idList") Collection<Integer> idList);

    List<BioSampleTestTwoResultDetailTb> selectAllByUniqueDbCode(@Param("uniqueDbCode") String uniqueDbCode);

    int insertBatch(@Param("bioSampleSampleTwoResultDetailTbCollection") Collection<BioSampleTestTwoResultDetailTb> bioSampleSampleTwoResultDetailTbCollection);

    List<BioSampleTestTwoResultDetailTb> selectAllBySampleCodeIn(@Param("sampleCodeList") Collection<String> sampleCodeList);
}




