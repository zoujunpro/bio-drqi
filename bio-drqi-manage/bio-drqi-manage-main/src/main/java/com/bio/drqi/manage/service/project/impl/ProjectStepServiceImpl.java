package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.cer.domain.CerFlowStepConf;
import com.bio.cer.mapper.CerFlowStepConfMapper;
import com.bio.cer.project.rsp.ProjectStepDetailRspDTO;
import com.bio.cer.service.project.ProjectStepService;
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
