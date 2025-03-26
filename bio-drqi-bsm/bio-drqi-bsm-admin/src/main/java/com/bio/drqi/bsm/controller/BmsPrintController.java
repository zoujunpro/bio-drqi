package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsPrintProductLabelReqDTO;
import com.bio.drqi.bsm.service.BmsPrintService;
import com.bio.drqi.manage.base.PrintRspDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.awt.*;

/**
 * 耗材标签打印
 */
@RestController
@RequestMapping("/print")
public class BmsPrintController {

    @Resource
    private BmsPrintService bmsPrintService;

    /**
     * 入库商品打印
     * @param bmsPrintProductLabelReqDTO
     * @return
     */
    @PostMapping("/productLabel")
    @WebLog(desc = "耗材标签打印-入库商品打印")
    public ResponseResult<PrintRspDTO> productLabel(@RequestBody BmsPrintProductLabelReqDTO bmsPrintProductLabelReqDTO) {
        return ResponseResult.getSuccess(bmsPrintService.productLabel(bmsPrintProductLabelReqDTO));
    }
}
