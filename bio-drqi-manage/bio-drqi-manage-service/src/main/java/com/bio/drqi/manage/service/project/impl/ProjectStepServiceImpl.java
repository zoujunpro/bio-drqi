package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.drqi.domain.CerFlowStepConf;
import com.bio.drqi.manage.service.project.ProjectStepService;
import com.bio.drqi.mapper.CerFlowStepConfMapper;
import com.bio.drqi.manage.project.rsp.ProjectStepDetailRspDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectStepServiceImpl implements ProjectStepService {

    @Resource
    private CerFlowStepConfMapper cerFlowStepConfMapper;
    @Override
    public List<ProjectStepDetailRspDTO> list() {
        List<ProjectStepDetailRspDTO> result=new ArrayList<>();
      List<CerFlowStepConf> cerFlowStepConfList=  cerFlowStepConfMapper.selectAllOrderByIdDesc();
      if(CollectionUtil.isNotEmpty(cerFlowStepConfList)){
          cerFlowStepConfList.forEach(cerFlowStepConf -> {
              ProjectStepDetailRspDTO projectStepDetailRspDTO=new ProjectStepDetailRspDTO();
              projectStepDetailRspDTO.setStepName(cerFlowStepConf.getFlowStepName());
              projectStepDetailRspDTO.setStepCode(cerFlowStepConf.getFlowStepCode());
              result.add(projectStepDetailRspDTO);
          });
      }
        return result;
    }
}
