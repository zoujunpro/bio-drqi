package com.bio.drqi.manage.feign;

import com.bio.common.core.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bio-plasmid-service")
public interface PlasmidAPi {

    @GetMapping("/bio/plasmid/detail")
    ResponseResult<Object> detail(@RequestParam String plasmid);
}
