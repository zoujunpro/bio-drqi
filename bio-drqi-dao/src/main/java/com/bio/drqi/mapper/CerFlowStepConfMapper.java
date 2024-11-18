package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerFlowStepConf;

public interface CerFlowStepConfMapper extends BaseMapper<CerFlowStepConf> {

    List<CerFlowStepConf> selectAllByFlowStepType(@Param("flowStepType") String flowStepType);

    List<CerFlowStepConf> selectAllByFlowStepCode(@Param("flowStepCode") String flowStepCode);

    List<CerFlowStepConf> selectAllOrderByIdDesc();

}