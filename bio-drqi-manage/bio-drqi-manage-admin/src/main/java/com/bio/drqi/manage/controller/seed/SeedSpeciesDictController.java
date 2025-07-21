package com.bio.drqi.manage.controller.seed;

import com.bio.base.base.PageDTO;
import com.bio.base.bio.req.SpeciesAddReqDTO;
import com.bio.base.bio.req.SpeciesEditDTO;
import com.bio.base.bio.rsp.SpeciesListRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
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
    @RequirePermissions("system:species")
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
     * 新增物种
     *
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "新增物种")
    @RequirePermissions("system:species:add")
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
    @RequirePermissions("system:species:delete")
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
    @RequirePermissions("system:species:edit")
    public ResponseResult<String> edit(@RequestBody SpeciesEditDTO speciesEditDTO) {
        seedSpeciesDictService.edit(speciesEditDTO);
        return ResponseResult.getSuccess("成功");
    }

}
