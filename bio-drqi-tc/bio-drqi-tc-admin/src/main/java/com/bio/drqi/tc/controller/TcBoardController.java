package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.rsp.TcBoardChartOneRspDTO;
import com.bio.drqi.tc.service.TcBoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 田测图表
 */
@RestController
@RequestMapping("tcBoard")
public class TcBoardController {

    @Resource
    private TcBoardService tcBoardService;

    @GetMapping("/chartOne")
    @WebLog(desc = "")
    public ResponseResult<TcBoardChartOneRspDTO> chartOne() {
        return ResponseResult.getSuccess(tcBoardService.chartOne());

    }

}
