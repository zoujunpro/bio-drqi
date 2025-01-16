package com.bio.drqi.manage.controller.seed;

import com.bio.drqi.seed.*;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.aspect.RequestLog;
import com.bio.drqi.manage.service.seed.SeedStoreService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 种子库库存
 */
@RestController
@RequestMapping("/seedStock")
public class SeedStockController {

    @Resource
    private SeedStoreService seedStoreService;

    /**
     * 分页查询种子库信息
     *
     * @param seedStockPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @RequirePermissions("seed:stock")
    @WebLog(desc = "分页查询种子库信息")
    public ResponseResult<PageInfo<SeedStockPageRspDTO>> listPage(@Validated @RequestBody SeedStockPageReqDTO seedStockPageReqDTO) {
        return ResponseResult.getSuccess(seedStoreService.listPage(seedStockPageReqDTO));
    }

    /**
     * 根据种子号查询种子信息
     *
     * @param seedNum
     * @return
     */
    @GetMapping("/querySeedByNum")
    @WebLog(desc = "根据种子号查询种子信息")
    public ResponseResult<SeedDetailRspDTO> querySeedByNum(@RequestParam @Validated String seedNum) {
        return ResponseResult.getSuccess(seedStoreService.querySeedByNum(seedNum));
    }


    /**
     * 查询所有种子（库存种数量还充足）
     *
     * @return
     */
    @PostMapping("/queryList")
    @WebLog(desc = "查询所有种子")
    public ResponseResult<PageInfo<SeedStockPageRspDTO>> queryList(@RequestBody SeedStockPageReqDTO seedStockPageReqDTO) {
        return ResponseResult.getSuccess(seedStoreService.queryList(seedStockPageReqDTO));
    }

    /**
     * 移库
     *
     * @return
     */
    @PostMapping("/moveStockLocationNum")
    @RequirePermissions("seed:stock:movelocation")
    @WebLog(desc = "移库")
    @RequestLog("移库")
    public ResponseResult<String> moveStockLocationNum(@RequestBody List<MoveStockLocationNumReqDTO> moveStockLocationNumReqDTOList) {
        seedStoreService.moveStockLocationNum(moveStockLocationNumReqDTOList);
        return ResponseResult.getSuccess("移库成功");
    }

    @PostMapping("/aliasName")
    @WebLog(desc = "命名别名")
    @RequirePermissions("seed:stock:aliasName")
    @RequestLog("命名别名")
    public ResponseResult<String> aliasName(@RequestBody AliasNameSeedReqDTO aliasNameSeedReqDTO) {
        seedStoreService.aliasName(aliasNameSeedReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 根据种子编号查询操作记录
     *
     * @param seedNum
     * @return
     */
    @GetMapping("/seedOperateDetail")
    @RequestLog("根据种子编号查询操作记录")
    @WebLog(desc = "根据种子编号查询操作记录")
    public ResponseResult<List<SeedOperateDetailRspDTO>> seedOperateDetail(@RequestParam @Validated String seedNum) {
        return ResponseResult.getSuccess(seedStoreService.seedOperateDetail(seedNum));
    }
}
