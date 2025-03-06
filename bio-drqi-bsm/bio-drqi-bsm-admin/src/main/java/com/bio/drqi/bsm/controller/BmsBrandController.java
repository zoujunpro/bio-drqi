package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsBrandAddReqDTO;
import com.bio.drqi.bsm.req.BmsBrandEditReqDTO;
import com.bio.drqi.bsm.req.BmsBrandListPageReqDTO;
import com.bio.drqi.bsm.req.BmsBrandQueryListReqDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsBrandQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsBrandService;
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
    public ResponseResult<PageInfo<BmsBrandListPageRspDTO>> listPage(@RequestBody BmsBrandListPageReqDTO bmsBrandListPageReqDTO) {
        return ResponseResult.getSuccess(bmsBrandService.listPage(bmsBrandListPageReqDTO));
    }

    /**
     * 品牌管理-条件查询
     *
     * @return
     */
    @PostMapping("/queryList")
    @WebLog(desc = "品牌管理-条件查询")
    public ResponseResult<List<BmsBrandQueryListRspDTO>> queryList(@RequestBody BmsBrandQueryListReqDTO bmsBrandQueryListReqDTO) {
        return ResponseResult.getSuccess(bmsBrandService.queryList(bmsBrandQueryListReqDTO));
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
    public ResponseResult<String> add(@RequestBody BmsBrandAddReqDTO bmsBrandAddReqDTO) {
        bmsBrandService.add(bmsBrandAddReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 品牌管理-删除
     *
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "品牌管理-删除")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        bmsBrandService.delete(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 品牌管理-修改
     *
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "品牌管理-修改")
    public ResponseResult<String> edit(@RequestBody BmsBrandEditReqDTO bmsBrandEditReqDTO) {
        bmsBrandService.edit(bmsBrandEditReqDTO);
        return ResponseResult.getSuccess("ok");
    }
}
