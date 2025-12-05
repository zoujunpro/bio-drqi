package com.bio.drqi.applet.service.impl;

import com.bio.drqi.applet.dto.req.QueryBySampleCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeProjectSampleTestRspDTO;
import com.bio.drqi.applet.service.SampleService;
import com.bio.drqi.applet.service.codescan.dto.ProjectSampleTestUniqueReqDTO;
import com.bio.drqi.applet.service.codescan.template.ProjectSampleTestCodeScanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SampleServiceImpl implements SampleService {

    @Resource
    private ProjectSampleTestCodeScanService projectSampleTestCodeScanService;

    @Override
    public ScanCodeProjectSampleTestRspDTO queryBySampleCode(QueryBySampleCodeReqDTO queryBySampleCodeReqDTO) {
        ProjectSampleTestUniqueReqDTO projectSampleTestUniqueReqDTO = new ProjectSampleTestUniqueReqDTO();
        projectSampleTestUniqueReqDTO.setVectorTaskCode(queryBySampleCodeReqDTO.getVectorTaskCode());
        projectSampleTestUniqueReqDTO.setSampleCode(queryBySampleCodeReqDTO.getSampleCode());
        return projectSampleTestCodeScanService.dealCodeContent(projectSampleTestUniqueReqDTO);
    }
}
