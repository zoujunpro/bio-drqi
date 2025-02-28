package com.bio.drqi.manage.controller.seed;

import com.bio.drqi.manage.service.seed.SeedStockOutService;
import com.bio.drqi.manage.seed.SeedStockOutReqDTO;
import com.bio.drqi.manage.seed.SeedStockOutRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 出库记录
 */
@RestController
@RequestMapping("/seedStockOut")
public class SeedStockOutController {

    @Resource
    private SeedStockOutService seedStockOutService;

    /**
     * 分页查询
     *
     * @param seedStockOutReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @RequirePermissions("seed:stockout")
    public ResponseResult<PageInfo<SeedStockOutRspDTO>> listPage(@RequestBody @Validated SeedStockOutReqDTO seedStockOutReqDTO) {
        return ResponseResult.getSuccess(seedStockOutService.listPage(seedStockOutReqDTO));
    }
}
