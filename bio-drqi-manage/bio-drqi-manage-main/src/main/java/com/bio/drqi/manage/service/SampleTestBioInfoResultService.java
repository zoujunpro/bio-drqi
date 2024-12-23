package com.bio.drqi.manage.service;

import java.util.Date;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.domain.CerSampleTestBioInfoResultTb;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.external.dto.BioResult;
import com.bio.drqi.external.dto.SampleTestBioInfoResultReqDTO;
import com.bio.drqi.external.dto.SampleTestBioInfoResultRspDTO;
import com.bio.drqi.mapper.CerSampleTestBioInfoResultTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SampleTestBioInfoResultService {

    @Resource
    private BioInfoClientApi bioInfoClientApi;

    @Resource
    private CerSampleTestBioInfoResultTbMapper cerSampleTestBioInfoResultTbMapper;

    public void synBioInfoResult(String runId, String sampleId, String sampleCode, String vectorTaskCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("RunID", runId);
        paramMap.put("sampleID", sampleId);
        BioResult<List<SampleTestBioInfoResultRspDTO>> bioInfoResultRspDTOBioResult = bioInfoClientApi.sampleTestBioInfoResult(paramMap);
        for (SampleTestBioInfoResultRspDTO sampleTestBioInfoResultRspDTO : bioInfoResultRspDTOBioResult.getData()) {
            CerSampleTestBioInfoResultTb cerSampleTestBioInfoResultTb = cerSampleTestBioInfoResultTbMapper.selectOneBySampleIdAndUniqueDbCode(sampleTestBioInfoResultRspDTO.getSampleID(), sampleTestBioInfoResultRspDTO.getUnique_DB_code());
            if (ObjectUtil.isNull(cerSampleTestBioInfoResultTb)) {
                cerSampleTestBioInfoResultTb = new CerSampleTestBioInfoResultTb();
                cerSampleTestBioInfoResultTb.setSampleCode(sampleCode);
                cerSampleTestBioInfoResultTb.setVectorTaskCode(vectorTaskCode);
                cerSampleTestBioInfoResultTb.setSampleId(sampleTestBioInfoResultRspDTO.getSampleID());
                cerSampleTestBioInfoResultTb.setUniqueDbCode(sampleTestBioInfoResultRspDTO.getUnique_DB_code());
                cerSampleTestBioInfoResultTb.setRunId(sampleTestBioInfoResultRspDTO.getRunID());
                cerSampleTestBioInfoResultTb.setHapId(sampleTestBioInfoResultRspDTO.getHapID());
                cerSampleTestBioInfoResultTb.setVartype(sampleTestBioInfoResultRspDTO.getVartype());
                cerSampleTestBioInfoResultTb.setMutate(sampleTestBioInfoResultRspDTO.getMutate());
                cerSampleTestBioInfoResultTb.setRatio(sampleTestBioInfoResultRspDTO.getRatio());
                cerSampleTestBioInfoResultTb.setCreateTime(new Date());
                cerSampleTestBioInfoResultTbMapper.insert(cerSampleTestBioInfoResultTb);
            }
        }
    }

}
