package com.bio.drqi.manage.controller.seed;

import com.bio.cer.seed.SeedDestructionPageReqDTO;
import com.bio.cer.seed.SeedDestructionPageRspDTO;
import com.bio.cer.service.seed.SeedDestructionService;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 销毁种子信息
 */
@RestController
@RequestMapping("/seedDestruction")
public class SeedDestructionController {

    @Resource
    private SeedDestructionService seedDestructionService;

    /**
     * 分页查询
     * @return
     */
    @PostMapping("/listPage")
    @RequirePermissions("seed:stockdestruction")
    public ResponseResult<PageInfo<SeedDestructionPageRspDTO>> listPage(@Validated @RequestBody SeedDestructionPageReqDTO seedDestructionPageReqDTO){
        return ResponseResult.getSuccess(seedDestructionService.listPage(seedDestructionPageReqDTO));
    }


}
