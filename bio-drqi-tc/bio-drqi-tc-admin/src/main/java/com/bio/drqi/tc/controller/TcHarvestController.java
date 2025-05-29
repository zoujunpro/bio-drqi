package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.service.TcHarvestService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 田测收获管理
 */
@RestController
@RequestMapping("/tcHarvest")
public class TcHarvestController {

    @Resource
    private TcHarvestService tcHarvestService;



    /**
     * 田测收获管理-生成收获excel
     */
    @PostMapping("/createHarvestExcel")
    @WebLog(desc = "田测收获管理-生成收获excel")
    public void createHarvestExcel(@RequestBody @Validated TcHarvestCreateHarvestExcelReqDTO tcHarvestCreateHarvestExcelReqDTO, HttpServletResponse httpServletResponse) {
        tcHarvestService.createHarvestExcel(tcHarvestCreateHarvestExcelReqDTO, httpServletResponse);
    }
}
