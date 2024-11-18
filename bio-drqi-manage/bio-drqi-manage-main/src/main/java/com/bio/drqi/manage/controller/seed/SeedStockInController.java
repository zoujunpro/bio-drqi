package com.bio.drqi.manage.controller.seed;

import com.bio.drqi.seed.*;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.aspect.RequestLog;
import com.bio.drqi.manage.service.seed.SeedStockInService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 入库记录
 */
@RestController
@RequestMapping("/seedStockIn")
public class SeedStockInController {

    @Resource
    private SeedStockInService seedStockInService;

    @Resource
    private OssService ossService;

    /**
     * 分页查询
     *
     * @param seedStockInReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @RequirePermissions("seed:stockin")
    @WebLog(desc = "分页查询")
    public ResponseResult<PageInfo<SeedStockInRspDTO>> listPage(@RequestBody @Validated SeedStockInReqDTO seedStockInReqDTO) {
        return ResponseResult.getSuccess(seedStockInService.listPage(seedStockInReqDTO));
    }

    /**
     * 种子库入库模板下载
     *
     * @param response
     */
    @GetMapping("/downSampleTemplate")
    public void downSampleTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "种子批量入库导入模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 解析入库excel
     *
     * @param parseSeedInExcelReqDTO
     * @return
     */
    @PostMapping("/parseSeedInExcel")
    @WebLog(desc = "解析入库excel")
    public ResponseResult<List<ParseSeedInExcelRspDTO>> parseSeedInExcel(@RequestBody ParseSeedInExcelReqDTO parseSeedInExcelReqDTO) {
        return ResponseResult.getSuccess(seedStockInService.parseSeedInExcel(parseSeedInExcelReqDTO));
    }

    /**
     * 种子入库（单个）
     *
     * @return
     */
    @PostMapping("/store")
    @WebLog(desc = "种子入库（单个）")
    @RequestLog("种子入库（单个）")
    public ResponseResult<String> store(@RequestBody @Validated SeedInStoreReqDTO seedInStoreReqDTO) {
        seedStockInService.store(seedInStoreReqDTO);
        return ResponseResult.getSuccess("成功");
    }

}
