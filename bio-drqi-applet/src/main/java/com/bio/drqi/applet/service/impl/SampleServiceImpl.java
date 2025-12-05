package com.bio.drqi.applet.service.impl;

import com.bio.drqi.applet.dto.req.QueryBySampleCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.service.SampleService;
import com.bio.drqi.applet.service.codescan.dto.unique.SampleTestUniqueReqDTO;
import com.bio.drqi.applet.service.codescan.template.SampleTestCodeScanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SampleServiceImpl implements SampleService {

    @Resource
    private SampleTestCodeScanService sampleTestCodeScanService;

    @Override
    public ScanCodeSampleTestRspDTO queryBySampleCode(QueryBySampleCodeReqDTO queryBySampleCodeReqDTO) {
        SampleTestUniqueReqDTO sampleTestUniqueReqDTO = new SampleTestUniqueReqDTO();
        sampleTestUniqueReqDTO.setSampleCode(queryBySampleCodeReqDTO.getSampleCode());
        return sampleTestCodeScanService.dealCodeContent(sampleTestUniqueReqDTO);
    }
}
