package com.bio.drqi.mapper;

import java.util.List;
import java.util.Collection;

import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSampleTestBioInfoResultTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【cer_sample_test_bio_info_result_tb】的数据库操作Mapper
 * @createDate 2024-12-24 15:46:16
 * @Entity com.bio.drqi.domain.CerSampleTestBioInfoResultTb
 */
public interface CerSampleTestBioInfoResultTbMapper extends BaseMapper<CerSampleTestBioInfoResultTb> {

    int deleteByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);

    List<CerSampleTestBioInfoResultTb> selectAllByIdIn(@Param("idList") Collection<Integer> idList);

    int deleteByApplyNo(@Param("applyNo") String applyNo);

    List<CerSampleTestBioInfoResultTb> selectAllByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);


    int insertBatch(@Param("cerSampleTestBioInfoResultTbCollection") Collection<CerSampleTestBioInfoResultTb> cerSampleTestBioInfoResultTbCollection);

    List<CerSampleTestBioInfoResultTb> selectAllBySampleCodeIn(@Param("sampleCodeList") Collection<String> sampleCodeList);

}




