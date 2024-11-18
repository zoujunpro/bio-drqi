package com.bio.drqi.manage.service.api;

import com.bio.cer.dto.req.ScanCodePlasmidReqDTO;
import com.bio.cer.dto.req.ScanCodeSampleTestReqDTO;
import com.bio.cer.dto.req.ScanCodeTransformReqDTO;
import com.bio.cer.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.cer.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.cer.dto.rsp.ScanCodeTransformRspDTO;
import com.bio.cer.project.rsp.ProjectListRspDTO;

public interface ProjectApiService {
    ProjectListRspDTO projectDetail(String projectCode);

    ScanCodePlasmidRspDTO plasmidDetail(ScanCodePlasmidReqDTO scanCodePlasmidReqDTO);

    ScanCodeTransformRspDTO transform(ScanCodeTransformReqDTO scanCodeTransformReqDTO);

    ScanCodeSampleTestRspDTO sampleTest(ScanCodeSampleTestReqDTO scanCodeSampleTestReqDTO);

}
