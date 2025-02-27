package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.dto.req.BmsBrandAddReqDTO;
import com.bio.drqi.bsm.dto.req.BmsBrandEditReqDTO;
import com.bio.drqi.bsm.dto.req.BmsBrandListPageReqDTO;
import com.bio.drqi.bsm.dto.rsp.BmsBrandListAllRspDTO;
import com.bio.drqi.bsm.dto.rsp.BmsBrandListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 品牌管理
 */
@RestController
@RequestMapping("/brand")
public class BmsBrandController {

    /**
     * 品牌管理-分页查询
     * @param bmsBrandListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "品牌管理-分页查询")
    public ResponseResult<PageInfo<BmsBrandListPageRspDTO>> listPage(BmsBrandListPageReqDTO bmsBrandListPageReqDTO){
        return null;
    }


    /**
     * 品牌管理-查询所有
     * @return
     */
    @GetMapping("/listAll")
    @WebLog(desc = "品牌管理-查询所有")
    public ResponseResult<List<BmsBrandListAllRspDTO>> listAll(){
        return null;
    }

    /**
     * 品牌管理-新增
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "品牌管理-新增")
    public ResponseResult<String> add(BmsBrandAddReqDTO bmsBrandAddReqDTO){
        return null;
    }

    /**
     * 品牌管理-删除
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "品牌管理-删除")
    public ResponseResult<String> delete(){
        return null;
    }

    /**
     * 品牌管理-修改
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "品牌管理-修改")
    public ResponseResult<String> edit(BmsBrandEditReqDTO bmsBrandEditReqDTO){
        return null;
    }
}
