package com.bio.drqi.mapper;

import java.util.List;

import com.bio.drqi.domain.CerSampleTestTb;
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

    TcSampleTestTb selectOneByExperimentCodeAndSampleCode(@Param("experimentCode") String experimentCode, @Param("sampleCode") String sampleCode);

    int updateIdentifyPrimerById(@Param("identifyPrimer") String identifyPrimer, @Param("id") Integer id);

    List<TcSampleTestTb> selectAllBySampleApplyNumAndSampleCodeIn(@Param("sampleApplyNum") String sampleApplyNum, @Param("sampleCodeList") Collection<String> sampleCodeList);

    List<TcSampleTestTb> selectAllBySampleApplyNumAndRegionNumAndSeedNum(@Param("sampleApplyNum") String sampleApplyNum, @Param("regionNum") String regionNum, @Param("seedNum") String seedNum);

}




