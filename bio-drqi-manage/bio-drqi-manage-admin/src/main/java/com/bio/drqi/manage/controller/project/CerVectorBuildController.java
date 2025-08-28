package com.bio.drqi.manage.controller.project;

import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.vector.req.CerVectorBuildListPageReqDTO;
import com.bio.drqi.manage.vector.rsp.CerVectorBuildListPageRspDTO;
import com.bio.drqi.manage.vector.rsp.VectorBuildDetailRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.manage.service.project.CerVectorBuildService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 载体构建
 */
@RestController
@RequestMapping("/vectorBuild")
public class CerVectorBuildController {

    @Resource
    private CerVectorBuildService cerVectorBuildService;


    /**
     * 载体构建-分页查询
     *
     * @param cerVectorBuildListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "载体构建-分页查询")
    public ResponseResult<PageInfo<CerVectorBuildListPageRspDTO>> listPage(@RequestBody @Validated CerVectorBuildListPageReqDTO cerVectorBuildListPageReqDTO) {
        return ResponseResult.getSuccess(cerVectorBuildService.listPage(cerVectorBuildListPageReqDTO));
    }

    /**
     * 载体构建详情
     *
     * @param vectorTaskId
     * @return
     */
    @GetMapping("/detail")
    public ResponseResult<List<VectorBuildDetailRspDTO>> detail(@RequestParam Integer vectorTaskId) {
        return ResponseResult.getSuccess(cerVectorBuildService.detail(vectorTaskId));
    }
}
