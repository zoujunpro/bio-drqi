package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.PlantSampleTestTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【plant_sample_test_tb(取样检测信息表)】的数据库操作Mapper
* @createDate 2025-11-19 10:41:36
* @Entity com.bio.drqi.domain.PlantSampleTestTb
*/
public interface PlantSampleTestTbMapper extends BaseMapper<PlantSampleTestTb> {

    int insertBatch(@Param("plantSampleTestTbCollection") Collection<PlantSampleTestTb> plantSampleTestTbCollection);

    int updateCheckResultByApplyNoAndCheckResultIsNull(@Param("checkResult") String checkResult, @Param("applyNo") String applyNo);

    List<PlantSampleTestTb> selectAllByApplyNo(@Param("applyNo") String applyNo);
}




