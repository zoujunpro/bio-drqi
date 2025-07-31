package com.bio.drqi.manage.controller.seed;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.drqi.manage.seed.SeedDictAddReqDTO;
import com.bio.drqi.manage.seed.SeedDictEditReqDTO;
import com.bio.drqi.manage.seed.SeedDictTreeListRspDTO;
import com.bio.drqi.manage.service.seed.SeedBioDictService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典管理
 */
@RestController
@RequestMapping("/seedDict")
public class SeedDictController {

    @Resource
    private SeedBioDictService seedBioDictService;

    /**
     * 种子库字典列表
     */
    @GetMapping("list")
    @RequirePermissions("seed:seedDict:list")
    public ResponseResult<List<SeedDictTreeListRspDTO>> list() {
        return ResponseResult.getSuccess(seedBioDictService.list());
    }

    /**
     * 种子库字典新增
     */
    @PostMapping("/add")
    @RequirePermissions("seed:seedDict:add")
    public ResponseResult<String> add(@RequestBody SeedDictAddReqDTO seedDictAddReqDTO) {
        seedBioDictService.add(seedDictAddReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 种子库字典删除
     */
    @GetMapping("/delete")
    @RequirePermissions("seed:seedDict:delete")
    public ResponseResult delete(@RequestParam Integer id) {
        seedBioDictService.delete(id);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 种子库字典编辑
     */
    @PostMapping("/edit")
    @RequirePermissions("seed:seedDict:edit")
    public ResponseResult edit(@RequestBody SeedDictEditReqDTO seedDictEditReqDTO) {
        seedBioDictService.edit(seedDictEditReqDTO);
        return ResponseResult.getSuccess("成功");
    }
}
