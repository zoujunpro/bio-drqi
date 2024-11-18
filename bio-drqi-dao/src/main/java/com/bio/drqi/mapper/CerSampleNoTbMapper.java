package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerSampleNoTb;

public interface CerSampleNoTbMapper extends BaseMapper<CerSampleNoTb> {

    List<CerSampleNoTb> selectByApplyBatchNo(@Param("applyBatchNo") String applyBatchNo);

}