package com.bio.drqi.applet.service;

import com.bio.drqi.applet.dto.req.QueryBySampleCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeProjectSampleTestRspDTO;

public interface SampleService {

    ScanCodeProjectSampleTestRspDTO queryBySampleCode(QueryBySampleCodeReqDTO queryBySampleCodeReqDTO);
}
