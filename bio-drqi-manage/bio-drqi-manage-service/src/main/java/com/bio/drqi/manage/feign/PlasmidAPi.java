package com.bio.drqi.manage.feign;

import com.bio.common.core.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = " bio-plasmid-service", path = "/plasmid")
public interface PlasmidAPi {

    @GetMapping("/detail")
    ResponseResult<Object> detail(@RequestParam String plasmid);

}
