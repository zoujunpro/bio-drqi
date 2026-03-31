package com.bio.drqi.tc.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcPollinationSingleNumListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationSingleNumListPageRspDTO;
import com.bio.drqi.tc.service.TcPollinationSingleNumService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 授粉单株编号管理
 */
@RestController
@RequestMapping("/tcPollinationSingleNum")
public class TcPollinationSingleNumController {

    @Resource
    private TcPollinationSingleNumService tcPollinationSingleNumService;

    /**
     * 授粉单株编号管理 - 分页查询
     *
     * @param tcPollinationSingleNumListPageReqDTO 查询条件
     * @return 分页结果
     */
    @PostMapping("/listPage")
    @WebLog(desc = "授粉单株编号管理 - 分页查询")
    @RequirePermissions("tc:tcPollinationSingleNum:listPage")
    public ResponseResult<PageInfo<TcPollinationSingleNumListPageRspDTO>> listPage(@RequestBody @Validated TcPollinationSingleNumListPageReqDTO tcPollinationSingleNumListPageReqDTO) {
        return ResponseResult.getSuccess(tcPollinationSingleNumService.listPage(tcPollinationSingleNumListPageReqDTO));
    }

    /**
     * 授粉单株编号管理 - 查询详情
     *
     * @param id 主键 ID
     * @return 详情信息
     */
    @GetMapping("/detail")
    @WebLog(desc = "授粉单株编号管理 - 查询详情")
    @RequirePermissions("tc:tcPollinationSingleNum:detail")
    public ResponseResult<TcPollinationSingleNumListPageRspDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(tcPollinationSingleNumService.detail(id));
    }


}
