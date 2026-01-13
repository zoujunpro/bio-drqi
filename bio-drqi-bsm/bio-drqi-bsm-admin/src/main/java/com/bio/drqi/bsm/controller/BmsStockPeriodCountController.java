package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsStockPeriodCountListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockPeriodCountListPageRspDTO;
import com.bio.drqi.bsm.service.BmsStockPeriodCountService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 库存期初期末管理
 */
@RestController
@RequestMapping("bmsStockPeriodCount")
public class BmsStockPeriodCountController {

    @Resource
    private BmsStockPeriodCountService bmsStockPeriodCountService;

    /**
     * 库存期初期末管理-分页查询
     *
     * @param bmsStockPeriodCountListPageReqDTO
     * @return
     */
    @PostMapping("listPage")
    @WebLog(desc = "库存期初期末管理-分页查询")
    public ResponseResult<PageInfo<BmsStockPeriodCountListPageRspDTO>> listPage(@Validated @RequestBody BmsStockPeriodCountListPageReqDTO bmsStockPeriodCountListPageReqDTO) {
        return ResponseResult.getSuccess(bmsStockPeriodCountService.listPage(bmsStockPeriodCountListPageReqDTO));

    }

    /**
     * 库存期初期末管理-导出
     *
     * @param bmsStockPeriodCountListPageReqDTO
     * @return
     */
    @PostMapping("exportExcel")
    @WebLog(desc = "库存期初期末管理-导出")
    public void exportExcel(@Validated @RequestBody BmsStockPeriodCountListPageReqDTO bmsStockPeriodCountListPageReqDTO, HttpServletResponse httpServletResponse) {
            bmsStockPeriodCountService.exportExcel(bmsStockPeriodCountListPageReqDTO,httpServletResponse);
    }
}
