package com.bio.drqi.manage.controller.seed;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.drqi.common.dto.PageDTO;
import com.bio.drqi.manage.seed.SeedQualityCheckAddReqDTO;
import com.bio.drqi.manage.seed.SeedQualityCheckEditReqDTO;
import com.bio.drqi.manage.seed.SeedQualityCheckRspDTO;
import com.bio.drqi.manage.service.seed.SeedQualityCheckService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

    /**
     * 种子库考种配置信息
     */
    @RestController
    @RequestMapping("seedQualityCheck")
    public class SeedQualityCheckController {

        @Resource
        private SeedQualityCheckService seedQualityCheckService;

        /**
         * 分页查询
         * @param pageDTO
         * @return
         */
        @PostMapping("/listPage")
        @RequirePermissions("seed:seedQualityCheck:listPage")
        public ResponseResult<PageInfo<SeedQualityCheckRspDTO>> listPage(@RequestBody PageDTO pageDTO) {
        PageInfo<SeedQualityCheckRspDTO> resultList = seedQualityCheckService.listPage(pageDTO);
        return ResponseResult.getSuccess(resultList);
    }

    /**
     *添加
     * @param seedQualityCheckAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @RequirePermissions("seed:seedQualityCheck:add")
    public ResponseResult<String> add(@RequestBody SeedQualityCheckAddReqDTO seedQualityCheckAddReqDTO){
        seedQualityCheckService.add(seedQualityCheckAddReqDTO);
        return ResponseResult.getSuccess("添加成功");
    }

    /**
     * 删除
     * @param seedQualityCheckEditReqDTO
     * @return
     */
    @PostMapping("/edit")
    @RequirePermissions("seed:seedQualityCheck:edit")
    public ResponseResult<String> edit(@RequestBody SeedQualityCheckEditReqDTO seedQualityCheckEditReqDTO){
        seedQualityCheckService.edit(seedQualityCheckEditReqDTO);
        return ResponseResult.getSuccess("删除成功");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @RequirePermissions("seed:seedQualityCheck:delete")
    public ResponseResult<String> delete(@RequestParam Integer id){
        seedQualityCheckService.delete(id);
        return ResponseResult.getSuccess("删除成功");
    }
}
