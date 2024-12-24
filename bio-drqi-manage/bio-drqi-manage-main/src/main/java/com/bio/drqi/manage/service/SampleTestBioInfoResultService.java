package com.bio.drqi.manage.service;

import cn.hutool.core.util.ObjectUtil;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.CerSampleTestBioInfoResultTb;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.external.dto.BioResult;
import com.bio.drqi.external.dto.SampleTestBioInfoResultRspDTO;
import com.bio.drqi.manage.dto.project.SampleTestBioInfoExcelDTO;
import com.bio.drqi.mapper.CerSampleTestBioInfoResultTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
public class SampleTestBioInfoResultService {

    @Resource
    private BioInfoClientApi bioInfoClientApi;

    @Resource
    private CerSampleTestBioInfoResultTbMapper cerSampleTestBioInfoResultTbMapper;

    public static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());


    public void readSampleTestBioInfoExcel(List<SampleTestBioInfoExcelDTO> sampleTestBioInfoExcelDTOList) {
        for (SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO : sampleTestBioInfoExcelDTOList) {
            log.info("开始拉去取样检测结果 sampleCode={},vectorTaskCode",sampleTestBioInfoExcelDTO.getSampleCode(),sampleTestBioInfoExcelDTO.getVectorTaskCode());
            int size = threadPool.getPoolSize();
            while (size > 1000) {
                try {
                    log.info("开始拉去取样检测结果 当前线程池中线程数越为：",size);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            threadPool.execute(() -> {
                synBioInfoResult(sampleTestBioInfoExcelDTO);
            });
        }
    }


    private void synBioInfoResult(SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("RunID", sampleTestBioInfoExcelDTO.getRunId());
        paramMap.put("sampleID", sampleTestBioInfoExcelDTO.getSampleId());
        BioResult<List<Map<String,String>>> bioInfoResultRspDTOBioResult = bioInfoClientApi.sampleTestBioInfoResult(paramMap);
        for (Map<String,String> map : bioInfoResultRspDTOBioResult.getData()) {
            CerSampleTestBioInfoResultTb cerSampleTestBioInfoResultTb = cerSampleTestBioInfoResultTbMapper.selectOneBySampleIdAndUniqueDbCode(map.get("sampleID"), map.get("Unique_DB_code"));
            if (ObjectUtil.isNull(cerSampleTestBioInfoResultTb)) {
                cerSampleTestBioInfoResultTb = new CerSampleTestBioInfoResultTb();
                cerSampleTestBioInfoResultTb.setSampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
                cerSampleTestBioInfoResultTb.setVectorTaskCode(sampleTestBioInfoExcelDTO.getVectorTaskCode());
                cerSampleTestBioInfoResultTb.setSampleId(map.get("sampleID"));
                cerSampleTestBioInfoResultTb.setUniqueDbCode(map.get("Unique_DB_code"));
                cerSampleTestBioInfoResultTb.setRunId(map.get("RunID"));
                cerSampleTestBioInfoResultTb.setHapId(map.get("HapID"));
                cerSampleTestBioInfoResultTb.setVarType(map.get("vartype"));
                cerSampleTestBioInfoResultTb.setMutate(map.get("mutate"));
                cerSampleTestBioInfoResultTb.setRatio(map.get("ratio"));
                cerSampleTestBioInfoResultTb.setCreateTime(new Date());
                cerSampleTestBioInfoResultTb.setMatchFlag(CerProjectContents.N);
                cerSampleTestBioInfoResultTbMapper.insert(cerSampleTestBioInfoResultTb);
            }
        }
    }

}
