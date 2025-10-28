package com.bio.drqi.manage.service.common;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.CerSampleTestBioInfoResultTb;
import com.bio.drqi.domain.CerSampleTestBioResultRef;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.external.dto.BioResult;
import com.bio.drqi.manage.dto.project.SampleTestBioInfoExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SynSampleTestResultService {

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(50, 50, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());


    @Resource
    private BioInfoClientApi bioInfoClientApi;

    public List<CerSampleTestBioInfoResultTb> synBioResult(List<CerSampleTestBioResultRef> cerSampleTestBioResultRefList) {
        List<CerSampleTestBioInfoResultTb> cerSampleTestBioInfoResultTbList = new ArrayList<>();
        AtomicInteger executeNum = new AtomicInteger(0);
        for (CerSampleTestBioResultRef cerSampleTestBioResultRef : cerSampleTestBioResultRefList) {
            while (threadPool.getPoolSize() > 1000) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            Future<List<CerSampleTestBioInfoResultTb>> future = threadPool.submit(() -> {
                return synBioInfoResult(executeNum, cerSampleTestBioResultRef);
            });
            List<CerSampleTestBioInfoResultTb> currentCerSampleTestBioInfoResultTbList = null;
            try {
                currentCerSampleTestBioInfoResultTbList = future.get();
            } catch (Exception e) {
                log.error("获取生信检测结果错误");
                throw new RuntimeException(e);
            }
            if (CollectionUtil.isNotEmpty(currentCerSampleTestBioInfoResultTbList)) {
                cerSampleTestBioInfoResultTbList.addAll(currentCerSampleTestBioInfoResultTbList);
            }

        }
        return cerSampleTestBioInfoResultTbList;
    }

    private List<CerSampleTestBioInfoResultTb> synBioInfoResult(AtomicInteger executeNum, CerSampleTestBioResultRef cerSampleTestBioResultRef) {
        log.info("获取生信检测结果 当前处理第{}数据 sampleCode={}", executeNum.get(), cerSampleTestBioResultRef.getSampleCode());
        if (StringUtils.isEmpty(cerSampleTestBioResultRef.getSampleId()) || StringUtils.isEmpty(cerSampleTestBioResultRef.getRunId())) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("RunID", cerSampleTestBioResultRef.getRunId());
        paramMap.put("sampleID", cerSampleTestBioResultRef.getSampleId());
        BioResult<List<Map<String, String>>> bioInfoResultRspDTOBioResult = bioInfoClientApi.sampleTestBioInfoResult(paramMap);
        List<CerSampleTestBioInfoResultTb> cerSampleTestBioInfoResultTbList = new ArrayList<>();
        for (Map<String, String> map : bioInfoResultRspDTOBioResult.getData()) {
            CerSampleTestBioInfoResultTb cerSampleTestBioInfoResultTb = new CerSampleTestBioInfoResultTb();
            cerSampleTestBioInfoResultTb.setApplyNo(cerSampleTestBioResultRef.getApplyNo());
            cerSampleTestBioInfoResultTb.setSampleCode(cerSampleTestBioResultRef.getSampleCode());
            cerSampleTestBioInfoResultTb.setVectorTaskCode(cerSampleTestBioResultRef.getVectorTaskCode());
            cerSampleTestBioInfoResultTb.setSampleId(map.get("sampleID"));
            cerSampleTestBioInfoResultTb.setUniqueDbCode(map.get("Unique_DB_code"));
            cerSampleTestBioInfoResultTb.setRunId(map.get("RunID"));
            cerSampleTestBioInfoResultTb.setHapId(map.get("HapID"));
            cerSampleTestBioInfoResultTb.setVarType(map.get("vartype"));
            cerSampleTestBioInfoResultTb.setMutate(map.get("mutate"));
            cerSampleTestBioInfoResultTb.setRatio(map.get("ratio"));
            cerSampleTestBioInfoResultTb.setConfirmStatus(map.get("ConfirmStatus"));
            cerSampleTestBioInfoResultTb.setResultKey(map.get("ResultKey"));
            cerSampleTestBioInfoResultTbList.add(cerSampleTestBioInfoResultTb);
        }
        executeNum.addAndGet(1);
        return cerSampleTestBioInfoResultTbList;
    }
}
