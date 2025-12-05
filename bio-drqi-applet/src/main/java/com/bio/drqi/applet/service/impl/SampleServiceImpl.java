package com.bio.drqi.applet.service.impl;

import com.bio.drqi.applet.dto.req.QueryBySampleCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.service.SampleService;
import com.bio.drqi.applet.service.codescan.dto.SampleTestUniqueReqDTO;
import com.bio.drqi.applet.service.codescan.template.ProjectSampleTestCodeScanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SampleServiceImpl implements SampleService {

    @Resource
    private ProjectSampleTestCodeScanService projectSampleTestCodeScanService;

    @Override
    public ScanCodeSampleTestRspDTO queryBySampleCode(QueryBySampleCodeReqDTO queryBySampleCodeReqDTO) {
        SampleTestUniqueReqDTO sampleTestUniqueReqDTO = new SampleTestUniqueReqDTO();
        sampleTestUniqueReqDTO.setVectorTaskCode(queryBySampleCodeReqDTO.getVectorTaskCode());
        sampleTestUniqueReqDTO.setSampleCode(queryBySampleCodeReqDTO.getSampleCode());
        return projectSampleTestCodeScanService.dealCodeContent(sampleTestUniqueReqDTO);
    }
}
