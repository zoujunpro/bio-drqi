package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsMoveOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsMoveOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.service.BmsMoveOrderDetailService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 库存调拨
 */
@RestController
@RequestMapping("/moveStock")
public class BmsMoveOrderDetailController {

    @Resource
    private BmsMoveOrderDetailService bmsMoveOrderDetailService;

    /**
     * 库存调拨-分页查询
     * @param bmsMoveOrderDetailListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "库存调拨-分页查询")
    public ResponseResult<PageInfo<BmsMoveOrderDetailListPageRspDTO>> listPage(@RequestBody @Validated BmsMoveOrderDetailListPageReqDTO bmsMoveOrderDetailListPageReqDTO){
        return ResponseResult.getSuccess(bmsMoveOrderDetailService.listPage(bmsMoveOrderDetailListPageReqDTO));
    }
}
