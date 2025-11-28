package com.bio.drqi.mapper;
import java.util.List;

import com.bio.drqi.domain.CerSampleTestTb;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.BioSampleTestTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_sample_test_tb(取样检测信息表)】的数据库操作Mapper
* @createDate 2025-11-19 10:41:36
* @Entity com.bio.drqi.domain.PlantSampleTestTb
*/
public interface BioSampleTestTbMapper extends BaseMapper<BioSampleTestTb> {

    List<BioSampleTestTb> selectAllByVectorTaskCodeAndSampleCode(@Param("vectorTaskCode") String vectorTaskCode, @Param("sampleCode") String sampleCode);

    List<BioSampleTestTb> selectSelective(BioSampleTestTb bioSampleTestTb);

    List<BioSampleTestTb> selectAllBySampleCode(@Param("sampleCode") String sampleCode);

    int insertBatch(@Param("bioSampleTestTbCollection") Collection<BioSampleTestTb> bioSampleTestTbCollection);

    BioSampleTestTb selectOneBySampleCodeOrderByIdDesc(@Param("sampleCode") String sampleCode);

    int updateNoCheckDataByApplyNoAndCheckResult(@Param("checkResult") String checkResult, @Param("checkUserId") Integer checkUserId, @Param("checkUserName") String checkUserName, @Param("testUserId") Integer testUserId, @Param("testUserName") String testUserName, @Param("applyNo") String applyNo, @Param("oldCheckResult") String oldCheckResult);
    List<BioSampleTestTb> selectAllByApplyNo(@Param("applyNo") String applyNo);

    List<BioSampleTestTb> selectAllBySampleCodeIn(@Param("sampleCodeList") Collection<String> sampleCodeList);

    int deleteByApplyNo(@Param("applyNo") String applyNo);

    List<BioSampleTestTb> selectAllByApplyNoAndSampleCodeIn(@Param("applyNo") String applyNo, @Param("sampleCodeList") Collection<String> sampleCodeList);

    int updateBatchById(@Param("list") List<BioSampleTestTb> bioSampleTestTbList);

    BioSampleTestTb selectOneByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);

    int updateIdentifyPrimerById(@Param("identifyPrimer") String identifyPrimer, @Param("id") Integer id);

    Integer selectCountByApplyNo(@Param("applyNo") String applyNo);

    Integer selectCountByApplyNoAndCheckResultIsNotNull(@Param("applyNo") String applyNo);

    Integer selectCountNum();


    List<BioSampleTestTb> selectCountNumByApplyNo(@Param("applyNo") String applyNo);


    int selectTestResultCount(@Param("applyNo") String applyNo);

    int selectNoTestResultCount(@Param("applyNo") String applyNo);

    //查询二代检测结String果
    int selectTowTestResultCount(@Param("applyNo") String applyNo);

    List<BioSampleTestTb> selectAllBySampleCodeLike(@Param("sampleCode") String sampleCode);

    BioSampleTestTb selectOneByVectorTaskCodeAndSampleCodeFirst(@Param("vectorTaskCode") String vectorTaskCode, @Param("sampleCode") String sampleCode);

    int updateTestUserIdAndTestUserNameById(@Param("testUserId") Integer testUserId, @Param("testUserName") String testUserName, @Param("id") Integer id);
}




