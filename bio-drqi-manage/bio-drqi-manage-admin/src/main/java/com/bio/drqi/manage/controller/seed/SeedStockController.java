package com.bio.drqi.manage.controller.seed;

import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.seed.*;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.seed.SeedStoreService;
import com.bio.drqi.manage.seedtask.SeedInDataReqDTO;
import com.bio.drqi.manage.seedtask.SeedTaskSeedNumRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
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
    @WebLog(desc = "根据种子编号查询操作记录")
    public ResponseResult<List<SeedOperateDetailRspDTO>> seedOperateDetail(@RequestParam @Validated String seedNum) {
        return ResponseResult.getSuccess(seedStoreService.seedOperateDetail(seedNum));
    }


    /**
     * 分页查询所在工单的入库记录明细
     *
     * @param seedInDataReqDTO
     * @return
     */
    @PostMapping("/seedInData")
    @WebLog(desc = "分页查询入库记录明细")
    public ResponseResult<PageInfo<SeedInStoreDTO.ExecuteFormContent>> seedInData(@RequestBody @Validated SeedInDataReqDTO seedInDataReqDTO) {
        return ResponseResult.getSuccess(seedStoreService.seedInData(seedInDataReqDTO));
    }

    /**
     * 查询所在工单所有种子编号
     *
     * @return
     */
    @GetMapping("/findAllSeedNum")
    @WebLog(desc = "种子库查询当前工单所有种子编号")
    public ResponseResult<List<SeedTaskSeedNumRspDTO>> findAllSeedNum(@RequestParam @Validated String taskNum) {
        List<SeedTaskSeedNumRspDTO> res = seedStoreService.findAllSeedNum(taskNum);
        return ResponseResult.getSuccess(res);
    }

    /**
     * 种子库-查询图谱
     *
     * @param seedNum
     * @return
     */
    @GetMapping("/findSeedMap")
    @WebLog(desc = "种子库-查询图谱")
    public ResponseResult<SeedMapRspDTO> findSeedMap(@RequestParam @Validated @NotBlank(message = "参数缺失") String seedNum) {
        return ResponseResult.getSuccess(seedStoreService.findSeedMap(seedNum));
    }


    /**
     * 种子库存-备注
     *
     * @param seedStockRemarkReqDTO
     * @return
     */
    @PostMapping("/remark")
    @WebLog(desc = "种子库存-备注")
    @RequestLog("种子库存-备注")
    @RequirePermissions("seed:stock:remark")
    public ResponseResult<String> remark(@RequestBody @Validated SeedStockRemarkReqDTO seedStockRemarkReqDTO) {
        seedStoreService.remark(seedStockRemarkReqDTO);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 种子库-查询某一个种子所有直系子代
     *
     * @param seedNum
     * @return
     */
    @GetMapping("/queryChildSeed")
    @WebLog(desc = "种子库-查询某一个种子所有直系子代")
    public ResponseResult<List<String>> queryChildSeed(String seedNum) {
        return ResponseResult.getSuccess(seedStoreService.queryChildSeed(seedNum));
    }

    /**
     * 种子库-查询种植列表
     *
     * @return
     */
    @GetMapping("/queryPlantList")
    @WebLog(desc = "种子库-查询种植列表")
    public ResponseResult<List<SeedStockQueryPlantListRspDTO>> queryPlantList(String seedNum) {
        return ResponseResult.getSuccess(seedStoreService.queryPlantList(seedNum));
    }
}
