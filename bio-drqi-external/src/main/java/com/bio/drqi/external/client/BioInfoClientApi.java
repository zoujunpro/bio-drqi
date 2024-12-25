package com.bio.drqi.external.client;

import com.bio.drqi.external.dto.BioResult;
import com.bio.drqi.external.dto.SampleTestBioInfoResultReqDTO;
import com.bio.drqi.external.dto.SampleTestBioInfoResultRspDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "bio-info-service", url = "http://172.16.14.2:10504")
public interface BioInfoClientApi {


    @PostMapping("/Drqi_search_sureRes")
    BioResult<List<Map<String,String>>> sampleTestBioInfoResult(@RequestBody Map<String,Object> paramMap);

    @GetMapping("/TJSearch_detail_result")
    Object sampleTestBioInfoResultDetail(@RequestParam Map<String,Object> paramMap);

}
