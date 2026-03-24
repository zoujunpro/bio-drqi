package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHarvestApplyListPageReqDTO;
import com.bio.drqi.tc.req.TcHavestDownSeedStockInExcelReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcHarvestApplyListPageRspDTO;
import com.bio.drqi.tc.service.TcHarvestApplyService;
import com.bio.drqi.tc.service.TcHarvestService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 收获详情管理
 */
@RestController
@RequestMapping("/tcHarvest")
public class TcHarvestController {

    @Resource
    private TcHarvestService tcHarvestService;


    /**
     * 收获详情管理-分页查询收获详情列表
     *
     * @param tcHarvestListPageDetailReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "收获详情管理-分页查询收获详情列表")
    public ResponseResult<PageInfo<TcHarvestListPageDetailRspDTO>> listPage(@RequestBody @Validated TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO) {
        return ResponseResult.getSuccess(tcHarvestService.listPage(tcHarvestListPageDetailReqDTO));
    }


    /**
     * 下载种子入库数据
     *
     * @param tcHavestDownSeedStockInExcelReqDTO
     */
    @PostMapping("downSeedStockInExcel")
    public void downSeedStockInExcel(@Validated @RequestBody TcHavestDownSeedStockInExcelReqDTO tcHavestDownSeedStockInExcelReqDTO, HttpServletResponse httpServletResponse) {
        tcHarvestService.downSeedStockInExcel(tcHavestDownSeedStockInExcelReqDTO,httpServletResponse);
    }
}
