package com.bio.drqi.applet.service.impl;

import com.bio.drqi.applet.dto.req.QueryBySampleCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeProjectSampleTestRspDTO;
import com.bio.drqi.applet.service.SampleService;
import com.bio.drqi.applet.service.codescan.dto.unique.ProjectSampleTestUniqueReqDTO;
import com.bio.drqi.applet.service.codescan.template.SampleTestCodeScanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SampleServiceImpl implements SampleService {

    @Resource
    private SampleTestCodeScanService sampleTestCodeScanService;

    @Override
    public ScanCodeProjectSampleTestRspDTO queryBySampleCode(QueryBySampleCodeReqDTO queryBySampleCodeReqDTO) {
        ProjectSampleTestUniqueReqDTO projectSampleTestUniqueReqDTO = new ProjectSampleTestUniqueReqDTO();
        projectSampleTestUniqueReqDTO.setSampleCode(queryBySampleCodeReqDTO.getSampleCode());
        return sampleTestCodeScanService.dealCodeContent(projectSampleTestUniqueReqDTO);
    }
}
