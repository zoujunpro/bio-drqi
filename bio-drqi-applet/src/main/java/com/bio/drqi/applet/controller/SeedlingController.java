package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.applet.dto.req.FindPlantFieldReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemainReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemoveReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingReportReqDTO;
import com.bio.drqi.applet.service.SeedlingService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/***
 * 苗操作相关接口
 */
@RestController
@RequestMapping("/seedling")
public class SeedlingController {

    @Resource
    private SeedlingService seedlingService;

    /**
     * 剔苗
     *
     * @return
     */
    @PostMapping("remove")
    @WebLog(desc = "剔苗")
    public ResponseResult<String> remove(@RequestBody SeedlingRemoveReqDTO seedlingRemoveReqDTO) {
        seedlingService.remove(seedlingRemoveReqDTO);
        return ResponseResult.getSuccess(null);
    }

    /**
     * 苗报备
     *
     * @return
     */
    @PostMapping("report")
    @WebLog(desc = "苗报备")
    public ResponseResult<String> report(@RequestBody SeedlingReportReqDTO seedlingReportReqDTO) {
        seedlingService.report(seedlingReportReqDTO);
        return ResponseResult.getSuccess(null);
    }

    /**
     * 查询字段
     * @param findPlantFieldReqDTO
     * @return
     */
    @PostMapping("/findPlantField")
    @WebLog(desc = "查询字段")
    public ResponseResult<List<Map<String, String>>> findPlantField(@RequestBody FindPlantFieldReqDTO findPlantFieldReqDTO) {
        return ResponseResult.getSuccess(seedlingService.findPlantField(findPlantFieldReqDTO));
    }


}
