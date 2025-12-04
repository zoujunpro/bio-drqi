package com.bio.drqi.manage.controller.plant;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.plant.rsp.PlantBoardCountPlantByVectorTaskEchartsRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantBoardCountRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantBoardPlantStatusEchartsRspDTO;
import com.bio.drqi.manage.service.plant.PlantBoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 种植图
 */
@RestController
@RequestMapping("plantBoard")
public class PlantBoardController {

    @Resource
    private PlantBoardService plantBoardService;

    /**
     * 种植图-种植状态图
     *
     * @return
     */
    @GetMapping("plantStatusEcharts")
    @WebLog(desc = "种植图-种植状态图")
    public ResponseResult<List<PlantBoardPlantStatusEchartsRspDTO>> plantStatusEcharts() {
        return ResponseResult.getSuccess(plantBoardService.plantStatusEcharts());
    }

    /**
     * 种植图-按照实施方案统计苗数量
     *
     * @return
     */
    @GetMapping("CountPlantByVectorTaskEcharts")
    @WebLog(desc = "种植图-按照实施方案统计苗数量")
    public ResponseResult<List<PlantBoardCountPlantByVectorTaskEchartsRspDTO>> CountPlantByVectorTaskEcharts() {

        return ResponseResult.getSuccess(plantBoardService.CountPlantByVectorTaskEcharts());
    }

    /**
     * 种植图-数量统计
     * @return
     */
    @GetMapping("count")
    @WebLog(desc = "种植图-数量统计")
    public ResponseResult<PlantBoardCountRspDTO>  count(){
    return ResponseResult.getSuccess(plantBoardService.count());
    }

}
