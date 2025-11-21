package com.bio.drqi.manage.controller.plant;

import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.plant.PlantSampleTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 分子取样检测
 */
@RestController
@RequestMapping("/plantSampleTest")
public class PlantSampleTestController {

    @Resource
    private PlantSampleTestService plantSampleTestService;


    @GetMapping("downRepeatSampleTemplate")
    @WebLog(desc = "分子取样检测-下载重复取样模板")
    public void downRepeatSampleTemplate(HttpServletResponse httpServletResponse) {
        plantSampleTestService.downRepeatSampleTemplate(httpServletResponse);
    }
}
