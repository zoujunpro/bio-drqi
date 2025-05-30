package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageRspDTO;
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

    @PostMapping("/listPage")
    @WebLog(desc = "田测收获管理-分页查询申请列表")
    public ResponseResult<PageInfo<TcHarvestListPageRspDTO>> listPage(@RequestBody TcHarvestListPageReqDTO tcHarvestListPageReqDTO) {
        return null;
    }

    /**
     * 田测收获管理-分页查询收获详情列表
     *
     * @param tcHarvestListPageDetailReqDTO
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "田测收获管理-分页查询收获详情列表")
    public ResponseResult<PageInfo<TcHarvestListPageDetailRspDTO>> listPageDetail(@RequestBody @Validated TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO) {
        return ResponseResult.getSuccess(tcHarvestService.listPageDetail(tcHarvestListPageDetailReqDTO));
    }


    /**
     * 田测收获管理-生成收获excel
     */
    @PostMapping("/createHarvestExcel")
    @WebLog(desc = "田测收获管理-生成收获excel")
    public void createHarvestExcel(@RequestBody @Validated TcHarvestCreateHarvestExcelReqDTO tcHarvestCreateHarvestExcelReqDTO, HttpServletResponse httpServletResponse) {
        tcHarvestService.createHarvestExcel(tcHarvestCreateHarvestExcelReqDTO, httpServletResponse);
    }
}
