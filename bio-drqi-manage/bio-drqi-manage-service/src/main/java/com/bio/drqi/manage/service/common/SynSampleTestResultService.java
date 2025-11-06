package com.bio.drqi.manage.service.common;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.BioSampleSampleTwoResultDetailTb;
import com.bio.drqi.domain.BioSampleSampleTwoResultTb;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.external.dto.BioResult;
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

    public List<BioSampleSampleTwoResultDetailTb> synBioResult(List<BioSampleSampleTwoResultTb> bioSampleSampleTwoResultTbList) {
        List<BioSampleSampleTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = new ArrayList<>();
        AtomicInteger executeNum = new AtomicInteger(0);
        for (BioSampleSampleTwoResultTb bioSampleSampleTwoResultTb : bioSampleSampleTwoResultTbList) {
            while (threadPool.getPoolSize() > 1000) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            Future<List<BioSampleSampleTwoResultDetailTb>> future = threadPool.submit(() -> {
                return synBioInfoResult(executeNum, bioSampleSampleTwoResultTb);
            });
            List<BioSampleSampleTwoResultDetailTb> currentBioSampleSampleTwoResultDetailTbList = null;
            try {
                currentBioSampleSampleTwoResultDetailTbList = future.get();
            } catch (Exception e) {
                log.error("获取生信检测结果错误");
                throw new RuntimeException(e);
            }
            if (CollectionUtil.isNotEmpty(currentBioSampleSampleTwoResultDetailTbList)) {
                bioSampleSampleTwoResultDetailTbList.addAll(currentBioSampleSampleTwoResultDetailTbList);
            }

        }
        return bioSampleSampleTwoResultDetailTbList;
    }

    private List<BioSampleSampleTwoResultDetailTb> synBioInfoResult(AtomicInteger executeNum, BioSampleSampleTwoResultTb bioSampleSampleTwoResultTb) {
        log.info("获取生信检测结果 当前处理第{}数据 sampleCode={}", executeNum.get(), bioSampleSampleTwoResultTb.getSampleCode());
        List<BioSampleSampleTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = new ArrayList<>();
        if (StringUtils.isEmpty(bioSampleSampleTwoResultTb.getSampleId()) || StringUtils.isEmpty(bioSampleSampleTwoResultTb.getRunId())) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("RunID", bioSampleSampleTwoResultTb.getRunId());
        paramMap.put("sampleID", bioSampleSampleTwoResultTb.getSampleId());
        BioResult<List<Map<String, String>>> bioInfoResultRspDTOBioResult = bioInfoClientApi.sampleTestBioInfoResult(paramMap);
        if(bioInfoResultRspDTOBioResult.isSuccess()){
            for (Map<String, String> map : bioInfoResultRspDTOBioResult.getData()) {
                BioSampleSampleTwoResultDetailTb bioSampleSampleTwoResultDetailTb = new BioSampleSampleTwoResultDetailTb();
                bioSampleSampleTwoResultDetailTb.setApplyNo(bioSampleSampleTwoResultTb.getApplyNo());
                bioSampleSampleTwoResultDetailTb.setSampleCode(bioSampleSampleTwoResultTb.getSampleCode());
                bioSampleSampleTwoResultDetailTb.setSampleId(map.get("sampleID"));
                bioSampleSampleTwoResultDetailTb.setUniqueDbCode(map.get("Unique_DB_code"));
                bioSampleSampleTwoResultDetailTb.setRunId(map.get("RunID"));
                bioSampleSampleTwoResultDetailTb.setHapId(map.get("HapID"));
                bioSampleSampleTwoResultDetailTb.setVarType(map.get("vartype"));
                bioSampleSampleTwoResultDetailTb.setMutate(map.get("mutate"));
                bioSampleSampleTwoResultDetailTb.setRatio(map.get("ratio"));
                bioSampleSampleTwoResultDetailTb.setConfirmStatus(map.get("ConfirmStatus"));
                bioSampleSampleTwoResultDetailTb.setResultKey(map.get("ResultKey"));
                bioSampleSampleTwoResultDetailTbList.add(bioSampleSampleTwoResultDetailTb);
            }
            bioSampleSampleTwoResultTb.setSynResult(BioDrQiContents.Y);
        }else {
            bioSampleSampleTwoResultTb.setSynResult(BioDrQiContents.N);
            bioSampleSampleTwoResultTb.setFailMessage(bioInfoResultRspDTOBioResult.getMessage());
        }

        executeNum.addAndGet(1);
        return bioSampleSampleTwoResultDetailTbList;
    }
}
