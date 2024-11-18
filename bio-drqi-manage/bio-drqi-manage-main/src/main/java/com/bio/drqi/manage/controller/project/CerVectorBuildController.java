package com.bio.drqi.manage.controller.project;

import com.bio.cer.vector.rsp.VectorBuildDetailRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.manage.service.project.CerVectorBuildService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 载体构建
 */
@RestController
@RequestMapping("/vectorBuild")
public class CerVectorBuildController {

    @Resource
    private CerVectorBuildService cerVectorBuildService;

    /**
     * 载体构建详情
     * @param vectorTaskId
     * @return
     */
    @GetMapping("/detail")
    public ResponseResult<VectorBuildDetailRspDTO> detail(@RequestParam Integer vectorTaskId){
        return ResponseResult.getSuccess(cerVectorBuildService.detail(vectorTaskId));
    }
}
