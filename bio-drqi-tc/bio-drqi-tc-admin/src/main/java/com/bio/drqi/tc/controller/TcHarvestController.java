package com.bio.drqi.tc.controller;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcHarvestListPageReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 田测收获管理
 */
@RestController
@RequestMapping("/tcHarvest")
public class TcHarvestController {

    /**
     * 田测收获管理-分页查询
     * @param tcHarvestListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc ="田测收获管理-分页查询" )
    public ResponseResult<PageInfo<TcHarvestListPageRspDTO>> listPage(@Validated @RequestBody TcHarvestListPageReqDTO tcHarvestListPageReqDTO){
        return null;
    }
}
