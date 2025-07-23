package com.bio.drqi.manage.controller.seed;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.common.dto.PageDTO;
import com.bio.drqi.manage.conf.SpeciesBreedListRspDTO;
import com.bio.drqi.manage.seed.SpeciesAddReqDTO;
import com.bio.drqi.manage.seed.SpeciesEditDTO;
import com.bio.drqi.manage.seed.SpeciesListRspDTO;
import com.bio.drqi.manage.service.seed.SeedSpeciesDictService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 物种管理
 */

@RestController
@RequestMapping("species")
public class SeedSpeciesDictController {

    @Resource
    private SeedSpeciesDictService seedSpeciesDictService;

    /**
     * 分页查询
     *
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "物种分页查询")
    @RequirePermissions("breed:species:listPage")
    public ResponseResult<PageInfo<SpeciesListRspDTO>> listPage(@RequestBody PageDTO pageDTO) {
        return ResponseResult.getSuccess(seedSpeciesDictService.listPage(pageDTO));
    }

    /**
     * 物种列表
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<List<SpeciesListRspDTO>> list() {
        return ResponseResult.getSuccess(seedSpeciesDictService.list());
    }

    /**
     * 获取品种物种
     *
     * @return
     */
    @GetMapping("speciesBreedList")
    public ResponseResult<List<SpeciesBreedListRspDTO>> speciesBreedList() {
        return ResponseResult.getSuccess(seedSpeciesDictService.speciesBreedList());
    }

    /**
     * 新增物种
     *
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "新增物种")
    @RequirePermissions("breed:species:add")
    public ResponseResult<String> add(@RequestBody SpeciesAddReqDTO speciesAddReqDTO) {
        seedSpeciesDictService.add(speciesAddReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 删除物种
     *
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "删除物种")
    @RequirePermissions("breed:species:delete")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        seedSpeciesDictService.delete(id);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 更新物种
     *
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "更新物种")
    @RequirePermissions("breed:species:edit")
    public ResponseResult<String> edit(@RequestBody SpeciesEditDTO speciesEditDTO) {
        seedSpeciesDictService.edit(speciesEditDTO);
        return ResponseResult.getSuccess("成功");
    }

}
