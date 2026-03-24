package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentListPageRspDTO;
import com.bio.drqi.tc.service.TcExperimentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 试验管理
 */
@RestController
@RequestMapping("/tcExperiment")
public class TcExperimentController {

    @Resource
    private TcExperimentService tcExperimentService;

    /**
     * 试验方案申请管理-条件查询试验设计
     *
     * @param
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "试验方案申请管理-条件查询试验设计")
    public ResponseResult<List<TcExperimentListPageRspDTO>> listPage(@Validated @RequestBody TcExperimentListPageReqDTO tcExperimentListPageReqDTO) {
        return ResponseResult.getSuccess(tcExperimentService.listPage(tcExperimentListPageReqDTO));
    }



}
