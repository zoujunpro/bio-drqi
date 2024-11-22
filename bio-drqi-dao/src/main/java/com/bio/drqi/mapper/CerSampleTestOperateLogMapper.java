package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSampleTestOperateLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sample_test_operate_log(对取样检测结果的剔苗保苗操作记录表)】的数据库操作Mapper
* @createDate 2024-11-22 15:20:08
* @Entity com.bio.drqi.domain.CerSampleTestOperateLog
*/
public interface CerSampleTestOperateLogMapper extends BaseMapper<CerSampleTestOperateLog> {

    CerSampleTestOperateLog selectOneByUniqueCode(@Param("uniqueCode") String uniqueCode);

}




