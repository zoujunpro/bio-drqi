package com.bio.drqi.manage.controller;

import com.bio.drqi.conf.SpeciesConfRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.manage.service.project.CerConfService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 配置项
 */
@RestController
@RequestMapping("conf")
public class CerConfController {

    @Resource
    private CerConfService cerConfService;

    /**
     * 物种配置项
     *
     * @return
     */
    @GetMapping("/speciesList")
    public ResponseResult<List<SpeciesConfRspDTO>> speciesList() {
        return ResponseResult.getSuccess(cerConfService.speciesList());
    }


}
