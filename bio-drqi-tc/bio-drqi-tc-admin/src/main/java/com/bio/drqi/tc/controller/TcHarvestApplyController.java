package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestApplyListPageReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestApplyListPageRspDTO;
import com.bio.drqi.tc.service.TcHarvestApplyService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 收获申请管理
 */
@RestController
@RequestMapping("/tcHarvestApply")
public class TcHarvestApplyController {

    @Resource
    private TcHarvestApplyService tcHarvestApplyService;

    /**
     * 收获申请管理-分页查询申请列表
     * @param tcHarvestApplyListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "收获申请管理-分页查询申请列表")
    @RequirePermissions("tc:tcHarvestApply:listPage")
    public ResponseResult<PageInfo<TcHarvestApplyListPageRspDTO>> listPage(@RequestBody TcHarvestApplyListPageReqDTO tcHarvestApplyListPageReqDTO) {
        return ResponseResult.getSuccess(tcHarvestApplyService.listPage(tcHarvestApplyListPageReqDTO));
    }




    /**
     * 收获申请管理-生成收获excel
     */
    @PostMapping("/createHarvestExcel")
    @WebLog(desc = "收获申请管理-生成收获excel")
    @RequirePermissions("tc:tcHarvestApply:createHarvestExcel")
    public void createHarvestExcel(@RequestBody @Validated TcHarvestCreateHarvestExcelReqDTO tcHarvestCreateHarvestExcelReqDTO, HttpServletResponse httpServletResponse) {
        tcHarvestApplyService.createHarvestExcel(tcHarvestCreateHarvestExcelReqDTO, httpServletResponse);
    }
}
