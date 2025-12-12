package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsBrandAddReqDTO;
import com.bio.drqi.bsm.req.BmsBrandEditReqDTO;
import com.bio.drqi.bsm.req.BmsBrandListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListPageRspDTO;
import com.bio.drqi.bsm.service.BmsBrandService;
import com.bio.drqi.common.aspect.RequestLog;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 品牌管理
 */
@RestController
@RequestMapping("/brand")
public class BmsBrandController {

    @Resource
    private BmsBrandService bmsBrandService;


    /**
     * 品牌管理-分页查询
     *
     * @param bmsBrandListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "品牌管理-分页查询")
    @RequirePermissions("bms:brand:listPage")
    public ResponseResult<PageInfo<BmsBrandListPageRspDTO>> listPage(@RequestBody BmsBrandListPageReqDTO bmsBrandListPageReqDTO) {
        return ResponseResult.getSuccess(bmsBrandService.listPage(bmsBrandListPageReqDTO));
    }
    /**
     * 品牌管理-查询所有
     *
     * @return
     */
    @GetMapping("/listAll")
    @WebLog(desc = "品牌管理-查询所有")
    public ResponseResult<List<BmsBrandListAllRspDTO>> listAll() {
        return ResponseResult.getSuccess(bmsBrandService.listAll());
    }

    /**
     * 品牌管理-新增
     *
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "品牌管理-新增")
    @RequirePermissions("bms:brand:add")
    @RequestLog("品牌管理-新增")
    public ResponseResult<String> add(@RequestBody BmsBrandAddReqDTO bmsBrandAddReqDTO) {
        bmsBrandService.add(bmsBrandAddReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 品牌管理-禁用
     *
     * @return
     */
    @GetMapping("/disable")
    @WebLog(desc = "品牌管理-禁用")
    @RequirePermissions("bms:brand:disable")
    @RequestLog("品牌管理-禁用")
    public ResponseResult<String> disable(@RequestParam Integer id) {
        bmsBrandService.disable(id);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 品牌管理-禁用
     *
     * @return
     */
    @GetMapping("/enable")
    @WebLog(desc = "品牌管理-禁用")
    @RequirePermissions("bms:brand:enable")
    @RequestLog("品牌管理-禁用")
    public ResponseResult<String> enable(@RequestParam Integer id) {
        bmsBrandService.enable(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 品牌管理-修改
     *
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "品牌管理-修改")
    @RequirePermissions("bms:brand:edit")
    @RequestLog("品牌管理-修改")
    public ResponseResult<String> edit(@RequestBody BmsBrandEditReqDTO bmsBrandEditReqDTO) {
        bmsBrandService.edit(bmsBrandEditReqDTO);
        return ResponseResult.getSuccess("ok");
    }
}
