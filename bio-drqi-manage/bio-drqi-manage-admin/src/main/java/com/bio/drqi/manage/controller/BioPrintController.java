package com.bio.drqi.manage.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.manage.base.PrintRspDTO;
import com.bio.drqi.manage.projectPrint.*;
import com.bio.drqi.manage.service.project.ProjectPrintService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 标签打印
 */
@RestController
@RequestMapping("/bioPrint")
public class BioPrintController {

    @Resource
    private ProjectPrintService projectPrintService;

    /**
     * 载体构建打印
     *
     * @return
     */
    @PostMapping("vectorBuildPrint")
    @WebLog(desc = "载体构建打印")
    @RequestLog("载体构建打印")
    public ResponseResult<List<PrintRspDTO>> vectorBuildPrint(@RequestBody @Validated VectorBuildPrintReqDTO vectorBuildPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.vectorBuildPrint(vectorBuildPrintReqDTO));
    }

    /**
     * 转化打印
     *
     * @return
     */
    @PostMapping("transFormPrint")
    @WebLog(desc = "转化打印")
    @RequestLog("转化打印")
    public ResponseResult<List<PrintRspDTO>> transFormPrint(@RequestBody @Validated TransFormPrintReqDTO transFormPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.transFormPrint(transFormPrintReqDTO));
    }


    /**
     * 取样编号打印
     *
     * @return
     */
    @PostMapping("samplePrint")
    @WebLog(desc = "取样编号打印")
    @RequestLog("取样编号打印")
    public ResponseResult<List<PrintRspDTO>> samplePrint(@RequestBody @Validated SamplePrintReqDTO samplePrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.samplePrint(samplePrintReqDTO));
    }

    /**
     * 96孔板签
     *
     * @return
     */
    @GetMapping("layoutPrint")
    @WebLog(desc = "96孔板签")
    @RequestLog("96孔板签")
    public ResponseResult<List<PrintRspDTO>> layoutPrint(@RequestParam @Validated String layoutNumber) {
        return ResponseResult.getSuccess(projectPrintService.layoutPrint(layoutNumber));
    }

    /**
     * 种植编号打印
     *
     * @return
     */
    @PostMapping("plantPrint")
    @WebLog(desc = "种植编号打印")
    @RequestLog("种植编号打印")
    public ResponseResult<List<PrintRspDTO>> plantPrint(@RequestBody @Validated PlantPrintReqDTO plantPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.plantPrint(plantPrintReqDTO));
    }

    /**
     * 移苗标签打印
     *
     * @return
     */
    @PostMapping("transPrint")
    @WebLog(desc = "移苗标签打印")
    @RequestLog("移苗标签打印")
    public ResponseResult<List<PrintRspDTO>> transPrint(@RequestBody @Validated TransPrintReqDTO transPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.transPrint(transPrintReqDTO));
    }

    /**
     * 组胚标签打印
     *
     * @return
     */
    @PostMapping("tissueEmbryoPrint")
    @WebLog(desc = "组胚标签打印")
    @RequestLog("组胚标签打印")
    public ResponseResult<List<PrintRspDTO>> tissueEmbryoPrint(@RequestBody @Validated TissueEmbryoPrintReqDTO transPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.tissueEmbryoPrint(transPrintReqDTO));
    }

    /**
     * 种植申请标签打印
     *
     * @return
     */
    @PostMapping("plantApplyPrint")
    @WebLog(desc = "种植申请标签打印")
    @RequestLog("种植申请标签打印")
    public ResponseResult<List<PrintRspDTO>> plantApplyPrint(@RequestBody @Validated BioPrintPlantApplyReqDTO bioPrintPlantApplyReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.plantApplyPrint(bioPrintPlantApplyReqDTO));
    }
    /**
     * 田间试验标签
     *
     * @return
     */
    @PostMapping("tcExperimentPrint")
    @WebLog(desc = "田间试验标签")
    @RequestLog("田间试验标签")
    public ResponseResult<List<PrintRspDTO>> tcExperimentPrint(@RequestBody @Validated BioPrintTcExperimentReqDTO bioPrintTcExperimentReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.tcExperimentPrint(bioPrintTcExperimentReqDTO));
    }

    /**
     * 农杆菌标签
     *
     * @return
     */
    @PostMapping("agrobacteriumPrint")
    @WebLog(desc = "农杆菌标签")
    @RequestLog("农杆菌标签")
    public ResponseResult<List<PrintRspDTO>> agrobacteriumPrint(@RequestBody @Validated BioAgrobacteriumPrintReqDTO bioAgrobacteriumPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.agrobacteriumPrint(bioAgrobacteriumPrintReqDTO));
    }
    /**
     *收获标签打印
     * @return
     */
    @PostMapping("harvestPrint")
    @WebLog(desc = "收获标签打印")
    @RequestLog("收获标签打印")
    public ResponseResult<List<PrintRspDTO>> harvestPrint(@RequestBody @Validated BioHarvestPrintReqDTO bioHarvestPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.harvestPrint(bioHarvestPrintReqDTO));
    }
}
