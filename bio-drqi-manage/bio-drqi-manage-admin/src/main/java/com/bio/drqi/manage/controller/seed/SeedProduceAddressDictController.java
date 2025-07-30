package com.bio.drqi.manage.controller.seed;

import com.bio.base.base.PageDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.seed.SeedProduceAddressDictAddDTO;
import com.bio.drqi.manage.seed.SeedProduceAddressDictEditDTO;
import com.bio.drqi.manage.seed.SeedProduceAddressDictListRspDTO;
import com.bio.drqi.manage.service.seed.SeedProduceAddressDictService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 地址管理
 */

@RestController
@RequestMapping("/seedProduceAddressDict")
public class SeedProduceAddressDictController {

    @Resource
    private SeedProduceAddressDictService seedProduceAddressDictService;

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    @WebLog(desc = "分页查询种子来源地址")
    @RequirePermissions("seed:seedProduceAddressDict:listPage")
    public ResponseResult<PageInfo<SeedProduceAddressDictListRspDTO>> listPage(@Validated @RequestBody PageDTO pageDTO) {
        return ResponseResult.getSuccess(seedProduceAddressDictService.listPage(pageDTO));
    }
    /**
     * 编辑
     */
    @PostMapping("/edit")
    @WebLog(desc = "编辑种子来源地址")
    @RequirePermissions("seed:seedProduceAddressDict:edit")
    public ResponseResult<String> edit(@Validated @RequestBody SeedProduceAddressDictEditDTO seedProduceAddressDictEditDTO) {
        seedProduceAddressDictService.edit(seedProduceAddressDictEditDTO);

        return ResponseResult.getSuccess("成功");
    }
    /**
     * 新增
     */
    @PostMapping("/add")
    @WebLog(desc = "新增种子来源地址")
    @RequirePermissions("seed:seedProduceAddressDict:add")
    public ResponseResult<String> add(@Validated @RequestBody SeedProduceAddressDictAddDTO seedProduceAddressDictAddDTO) {
        seedProduceAddressDictService.add(seedProduceAddressDictAddDTO);
        return ResponseResult.getSuccess("成功");
    }
    /**
     * 删除
     */
    @GetMapping("/delete")
    @WebLog(desc = "删除种子来源地址")
    @RequirePermissions("seed:seedProduceAddressDict:delete")
    public ResponseResult<String> delete(@RequestParam  Integer id) {
        seedProduceAddressDictService.delete(id);
        return ResponseResult.getSuccess("成功");
    }

}
