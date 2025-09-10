package com.bio.drqi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import java.util.Collection;

import com.bio.drqi.domain.CerSampleTestTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【cer_sample_test_tb(取样检测信息表)】的数据库操作Mapper
 * @createDate 2023-11-14 14:27:54
 * @Entity com.bio.cer.domain.CerSampleTestTb
 */
public interface CerSampleTestTbMapper extends BaseMapper<CerSampleTestTb> {

    List<CerSampleTestTb> selectAllByProjectId(@Param("projectId") Integer projectId);


    int insertBatch(@Param("cerSampleTestTbCollection") Collection<CerSampleTestTb> cerSampleTestTbCollection);

    List<CerSampleTestTb> selectSelective(CerSampleTestTb cerSampleTestTb);

    List<CerSampleTestTb> selectAllByVectorTaskIdAndSampleCodeIn(@Param("vectorTaskId") Integer vectorTaskId, @Param("sampleCodeList") Collection<String> sampleCodeList);

    List<CerSampleTestTb> selectAllByVectorTaskId(@Param("vectorTaskId") Integer vectorTaskId);

    List<Integer> selectVectorTaskIdByProjectId(@Param("projectId") Integer projectId);

    int updateBatchById(@Param("list") List<CerSampleTestTb> cerSampleTestTbList);

    List<CerSampleTestTb> selectAllByApplyNoAndSampleCodeIn(@Param("applyNo") String applyNo, @Param("sampleCodeList") Collection<String> sampleCodeList);

    List<CerSampleTestTb> selectAllByApplyNo(@Param("applyNo") String applyNo);

    CerSampleTestTb selectOneByApplyNoAndSampleCode(@Param("applyNo") String applyNo, @Param("sampleCode") String sampleCode);

    List<CerSampleTestTb> selectAllByVectorTaskCodeAndSampleCode(@Param("vectorTaskCode") String vectorTaskCode, @Param("sampleCode") String sampleCode);


    CerSampleTestTb selectOneByVectorTaskCodeAndSampleCodeFirst(@Param("vectorTaskCode") String vectorTaskCode, @Param("sampleCode") String sampleCode);

    CerSampleTestTb selectOneByProjectCodeAndSampleCodeFirst(@Param("projectCode") String projectCode, @Param("sampleCode") String sampleCode);

    int updateCheckResultAndCheckTaskNumAndCheckUserIdAndCheckUserNameByApplyNo(@Param("checkResult") String checkResult, @Param("checkTaskNum") String checkTaskNum, @Param("checkUserId") Integer checkUserId, @Param("checkUserName") String checkUserName, @Param("applyNo") String applyNo);


    int updateTaskStatusByApplyNo(@Param("taskStatus") String taskStatus, @Param("applyNo") String applyNo);


    int deleteByApplyNo(@Param("applyNo") String applyNo);

    int updateIdentifyPrimerById(@Param("identifyPrimer") String identifyPrimer, @Param("id") Integer id);

    Integer selectCountByApplyNo(@Param("applyNo") String applyNo);

    Integer selectCountByApplyNoAndCheckResultIsNotNull(@Param("applyNo") String applyNo);

    Integer selectCountNum();


    List<CerSampleTestTb> selectCountByMonth(@Param("year") String year);

    CerSampleTestTb selectOneByUniqueCode(@Param("uniqueCode") String uniqueCode);

    int updateCheckResultByApplyNoAndCheckResultIsNull(@Param("checkResult") String checkResult, @Param("applyNo") String applyNo);

    List<CerSampleTestTb> selectCountNumByApplyNo(@Param("applyNo") String applyNo);


    List<CerSampleTestTb> selectAllBySampleCode(@Param("sampleCode") String sampleCode);


    List<CerSampleTestTb> selectAllByTransformCode(@Param("transformCode") String transformCode);

    List<CerSampleTestTb> selectAllBySampleCodeLike(@Param("sampleCode") String sampleCode);


    int updateTargetFlagByApplyNoAndSampleCodeIn(@Param("targetFlag") String targetFlag, @Param("applyNo") String applyNo, @Param("sampleCodeList") Collection<String> sampleCodeList);
}




