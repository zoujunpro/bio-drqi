package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHarvestApplyListPageReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcHarvestApplyListPageRspDTO;
import com.bio.drqi.tc.service.TcHarvestApplyService;
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
 * 收获详情管理
 */
@RestController
@RequestMapping("/tcHarvestApply")
public class TcHarvestController {

    @Resource
    private TcHarvestService tcHarvestService;


    /**
     * 收获详情管理-分页查询收获详情列表
     *
     * @param tcHarvestListPageDetailReqDTO
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "收获详情管理-分页查询收获详情列表")
    public ResponseResult<PageInfo<TcHarvestListPageDetailRspDTO>> listPage(@RequestBody @Validated TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO) {
        return ResponseResult.getSuccess(tcHarvestService.listPage(tcHarvestListPageDetailReqDTO));
    }

}
