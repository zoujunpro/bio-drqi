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

    int deleteByTwoResultId(@Param("twoResultId") Integer twoResultId);

    List<BioSampleTestTwoResultDetailTb> selectAllByTwoResultIdAndConfirmStatus(@Param("twoResultId") Integer twoResultId, @Param("confirmStatus") String confirmStatus);


    List<BioSampleTestTwoResultDetailTb> selectAllByTwoResultId(@Param("twoResultId") Integer twoResultId);

    int deleteByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);

    List<BioSampleTestTwoResultDetailTb> selectAllByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);

    int deleteByIdIn(@Param("idList") Collection<Integer> idList);

    List<BioSampleTestTwoResultDetailTb> selectAllByUniqueDbCode(@Param("uniqueDbCode") String uniqueDbCode);

    int insertBatch(@Param("bioSampleTestTwoResultDetailTbCollection") Collection<BioSampleTestTwoResultDetailTb> bioSampleTestTwoResultDetailTbCollection);

    List<BioSampleTestTwoResultDetailTb> selectAllBySampleCodeIn(@Param("sampleCodeList") Collection<String> sampleCodeList);

}




