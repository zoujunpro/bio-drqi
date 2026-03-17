package com.bio.drqi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import java.util.Collection;

import com.bio.drqi.domain.TcSampleTestTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【tc_sample_test_tb(田测取样检测表)】的数据库操作Mapper
 * @createDate 2025-05-12 10:45:09
 * @Entity com.bio.drqi.domain.TcSampleTestTb
 */
public interface TcSampleTestTbMapper extends BaseMapper<TcSampleTestTb> {

    int insertBatch(@Param("tcSampleTestTbCollection") Collection<TcSampleTestTb> tcSampleTestTbCollection);

    int updateBatchById(@Param("list") List<TcSampleTestTb> tcSampleTestTbCollection);

    List<TcSampleTestTb> selectSelective(TcSampleTestTb tcSampleTestTb);

    List<TcSampleTestTb> selectAllBySampleApplyNum(@Param("sampleApplyNum") String sampleApplyNum);

    TcSampleTestTb selectOneBySampleApplyNumAndSampleCode(@Param("sampleApplyNum") String sampleApplyNum, @Param("sampleCode") String sampleCode);

    TcSampleTestTb selectOneBySampleApplyNumAndTcSampleCode(@Param("sampleApplyNum") String sampleApplyNum, @Param("tcSampleCode") String tcSampleCode);

    int updateIdentifyPrimerById(@Param("identifyPrimer") String identifyPrimer, @Param("id") Integer id);


    List<TcSampleTestTb> selectAllByTcSampleCode(@Param("tcSampleCode") String tcSampleCode);
    List<TcSampleTestTb> selectAllBySampleApplyNumAndSampleCodeIn(@Param("sampleApplyNum") String sampleApplyNum, @Param("sampleCodeList") Collection<String> sampleCodeList);


    List<TcSampleTestTb> selectAllBySampleCodeInAndApplyType(@Param("sampleCodeList") Collection<String> sampleCodeList, @Param("applyType") String applyType);

    int deleteBySampleApplyNum(@Param("sampleApplyNum") String sampleApplyNum);

    TcSampleTestTb selectOneByUniqueCode(@Param("uniqueCode") String uniqueCode);


    List<TcSampleTestTb> selectAllByExperimentNum(@Param("experimentNum") String experimentNum);

    List<TcSampleTestTb> selectAllBySampleApplyNumAndSeedNumAndRegionNumAndCheckResult(@Param("sampleApplyNum") String sampleApplyNum, @Param("seedNum") String seedNum, @Param("regionNum") String regionNum, @Param("checkResult") String checkResult);

    int updateTargetFlagBySampleApplyNum(@Param("targetFlag") String targetFlag, @Param("sampleApplyNum") String sampleApplyNum);

    int updateTargetFlagBySampleApplyNumAndSampleCodeIn(@Param("targetFlag") String targetFlag, @Param("sampleApplyNum") String sampleApplyNum, @Param("sampleCodeList") Collection<String> sampleCodeList);

    int updateCheckResultBySampleApplyNumAndSampleCodeIn(@Param("checkResult") String checkResult, @Param("sampleApplyNum") String sampleApplyNum, @Param("sampleCodeList") Collection<String> sampleCodeList);

    int updateCheckResultBySampleApplyNumAndSampleCodeNotIn(@Param("checkResult") String checkResult, @Param("sampleApplyNum") String sampleApplyNum, @Param("sampleCodeList") Collection<String> sampleCodeList);
}




