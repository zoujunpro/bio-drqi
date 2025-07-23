package com.bio.drqi.manage.controller.seed;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.seed.BreedAddReqDTO;
import com.bio.drqi.manage.seed.BreedEditReqDTO;
import com.bio.drqi.manage.seed.BreedListReqDTO;
import com.bio.drqi.manage.seed.BreedListRspDTO;
import com.bio.drqi.manage.service.seed.SeedBreedDictService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 品种管理
 */

@RestController
@RequestMapping("breed")
public class SeedBreedDictController {

    @Resource
    private SeedBreedDictService seedBreedDictService;


    /**
     * 分页查询
     * @return
     */
    @PostMapping("listPage")
    @WebLog(desc = "分页查询")
    @RequirePermissions("seed:breed:listPage")
    public ResponseResult<PageInfo<BreedListRspDTO>> listPage(@RequestBody BreedListReqDTO breedListReqDTO){
        return ResponseResult.getSuccess(seedBreedDictService.listPage(breedListReqDTO));
    }





    /**
     * 品种列表
     * @param speciesCode
     * @return
     */
    @GetMapping("list")
    @WebLog(desc = "品种列表")
    public ResponseResult<List<BreedListRspDTO>> list(@RequestParam String speciesCode){
        return ResponseResult.getSuccess(seedBreedDictService.list(speciesCode));
    }




    /**
     * 新增品种
     * @param breedAddReqDTO
     * @return
     */
    @PostMapping("add")
    @WebLog(desc = "新增品种")
    @RequirePermissions("seed:breed:add")
    public ResponseResult add(@RequestBody BreedAddReqDTO breedAddReqDTO){
        seedBreedDictService.add(breedAddReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 删除品种
     * @param id
     * @return
     */
    @GetMapping("delete")
    @WebLog(desc = "删除品种")
    @RequirePermissions("seed:breed:delete")
    public ResponseResult delete(@RequestParam Integer id){
        seedBreedDictService.delete(id);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 更新品种
     * @param breedEditReqDTO
     * @return
     */
    @PostMapping("edit")
    @WebLog(desc = "更新品种")
    @RequirePermissions("seed:breed:edit")
    public ResponseResult edit(@RequestBody BreedEditReqDTO breedEditReqDTO){
        seedBreedDictService.edit(breedEditReqDTO);
        return ResponseResult.getSuccess("成功");
    }
}
