package com.bio.drqi.bsm.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.bsm.req.BmsProjectQueryAllReqDTO;
import com.bio.drqi.bsm.service.BmsProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 耗材管理项目字典表
 */
@RestController
@RequestMapping("/bmsProject")
public class BmsProjectController {


    @Resource
    private BmsProjectService bmsProjectService;

    /**
     * 查询所有
     * @return
     */
    @GetMapping("/queryAll")
    public ResponseResult<List<BmsProjectQueryAllReqDTO>> queryAll() {
        return ResponseResult.getSuccess(bmsProjectService.queryAll());
    }
}
