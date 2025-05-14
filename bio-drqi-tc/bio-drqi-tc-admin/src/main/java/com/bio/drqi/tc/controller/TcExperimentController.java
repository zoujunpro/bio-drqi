package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentListDetailRspDTO;
import com.bio.drqi.tc.rsp.TcExperimentListPageRspDTO;
import com.bio.drqi.tc.service.TcExperimentService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 试验方案管理
 */
@RestController
@RequestMapping("/tcExperiment")
public class TcExperimentController {

    @Resource
    private TcExperimentService tcExperimentService;

    /**
     * 试验方案申请管理-分页查询
     * @param tcExperimentListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "试验方案申请管理-分页查询")
    public ResponseResult<PageInfo<TcExperimentListPageRspDTO>> listPage(@Validated @RequestBody TcExperimentListPageReqDTO tcExperimentListPageReqDTO) {
        return ResponseResult.getSuccess(tcExperimentService.listPage(tcExperimentListPageReqDTO));
    }
    /**
     * 试验方案申请管理-文件下载
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/downTemplate")
    @WebLog(desc = "试验方案申请管理-文件下载")
    public void downTemplate(HttpServletResponse httpServletResponse) {
        tcExperimentService.downTemplate(httpServletResponse);
    }

    /**
     * 试验方案申请管理-田间设计列表
     * @param experimentCode
     * @return
     */
    @GetMapping("/listDetail")
    @WebLog(desc = "试验方案申请管理-田间设计列表")
    public ResponseResult<List<TcExperimentListDetailRspDTO>> listDetail(@RequestParam @Validated String experimentCode){
        return ResponseResult.getSuccess(tcExperimentService.listDetail(experimentCode));
    }

}
