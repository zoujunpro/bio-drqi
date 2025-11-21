package com.bio.drqi.plant.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.plant.service.PlantSampleTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * CER取样检测
 */
@RestController
@RequestMapping("plantSampleTest")
public class PlantSampleTestController {

    @Resource
    private PlantSampleTestService plantSampleTestService;

    /**
     * CER取样检测-重复取样模板下载
     * @param httpServletResponse
     */
    @GetMapping("downRepeatSampleTemplate")
    @WebLog(desc = "CER取样检测-重复取样模板下载")
    public void downRepeatSampleTemplate(HttpServletResponse httpServletResponse) {
        plantSampleTestService.downRepeatSampleTemplate(httpServletResponse);
    }


}
