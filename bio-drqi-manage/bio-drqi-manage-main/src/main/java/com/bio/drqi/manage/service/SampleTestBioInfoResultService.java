package com.bio.drqi.manage.service;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.external.dto.SampleTestBioInfoResultReqDTO;
import com.bio.drqi.external.dto.SampleTestBioInfoResultRspDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SampleTestBioInfoResultService {

    @Resource
    private BioInfoClientApi bioInfoClientApi;

    public void synBioInfoResult(String runId, String sampleId) {
        SampleTestBioInfoResultReqDTO sampleTestBioInfoResultReqDTO = new SampleTestBioInfoResultReqDTO();
        sampleTestBioInfoResultReqDTO.setRunID(runId);
        sampleTestBioInfoResultReqDTO.setSampleID(sampleId);
        SampleTestBioInfoResultRspDTO sampleTestBioInfoResultRspDTO = bioInfoClientApi.sampleTestBioInfoResult(sampleTestBioInfoResultReqDTO);
        System.out.println(JSONUtil.toJsonStr(sampleTestBioInfoResultRspDTO));

    }

}
