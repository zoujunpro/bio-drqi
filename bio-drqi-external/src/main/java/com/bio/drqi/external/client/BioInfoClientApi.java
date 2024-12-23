package com.bio.drqi.external.client;

import com.bio.drqi.external.dto.SampleTestBioInfoResultReqDTO;
import com.bio.drqi.external.dto.SampleTestBioInfoResultRspDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bio-info-service", url = "http://172.16.14.2:10504")
public interface BioInfoClientApi {


    @PostMapping("/Drqi_search_sureRes")
    SampleTestBioInfoResultRspDTO sampleTestBioInfoResult(@RequestBody SampleTestBioInfoResultReqDTO sampleTestBioInfoResultReqDTO);

}
