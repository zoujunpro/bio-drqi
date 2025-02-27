package com.bio.drqi.manage.controller.seed;


import com.bio.core.common.aspect.RequestLog;
import com.bio.drqi.base.PrintRspDTO;
import com.bio.drqi.manage.service.seed.SeedPrintService;
import com.bio.drqi.print.SeedInPrintReqDTO;
import com.bio.drqi.print.SeedOutPrintReqDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 种子库打印
 */
@RestController
@RequestMapping("/seedPrint")
public class SeedPrintController {

    @Resource
    private SeedPrintService seedPrintService;

    /***
     * 出库打印
     * @param seedOutPrintReqDTO
     * @return
     */
    @PostMapping("/seedOutLabelPrint")
    @WebLog(desc = "出库打印")
    @RequestLog("出库打印")
    public ResponseResult<List<PrintRspDTO>> seedOutLabelPrint(@RequestBody @Validated SeedOutPrintReqDTO seedOutPrintReqDTO) {

        return ResponseResult.getSuccess(seedPrintService.seedOutLabelPrint(seedOutPrintReqDTO));
    }

    /**
     * 入库打印
     * @param seedInPrintReqDTO
     * @return
     */
    @PostMapping("/seedInLabelPrint")
    @WebLog(desc = "入库打印")
    @RequestLog("入库打印")
    public ResponseResult<List<PrintRspDTO>> seedInLabelPrint(@RequestBody @Validated SeedInPrintReqDTO seedInPrintReqDTO) {

        return ResponseResult.getSuccess(seedPrintService.seedInLabelPrint(seedInPrintReqDTO));
    }

}
