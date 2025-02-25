package com.bio.drqi.manage.controller.seed;

import com.bio.drqi.manage.service.seed.SeedDestructionService;
import com.bio.drqi.seed.SeedDestructionPageReqDTO;
import com.bio.drqi.seed.SeedDestructionPageRspDTO;
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
