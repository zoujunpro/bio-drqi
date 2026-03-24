package com.bio.drqi.tc.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 授粉详情管理
 */
@RestController
@RequestMapping("/tcPollination")
public class TcPollinationController {

    @Resource
    private TcPollinationService tcPollinationService;


    /**
     * 授粉详情管理-授授粉详情分页查询
     *
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "授粉管理-授授粉详情分页查询")
    public ResponseResult<PageInfo<TcPollinationListPageDetailRspDTO>> listPage(@RequestBody @Validated TcPollinationListPageDetailReqDTO tcPollinationListPageDetailReqDTO) {
        return ResponseResult.getSuccess(tcPollinationService.listPage(tcPollinationListPageDetailReqDTO));
    }

}
