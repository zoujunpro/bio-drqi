package com.bio.drqi.manage.service.plant;

import com.bio.drqi.manage.plant.req.PlantMultipleStockListPageReqDTO;
import com.bio.drqi.manage.plant.req.PlantMultipleStockQueryListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantMultipleStockListPageRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantMultipleStockQueryListRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface PlantMultipleStockService {


    /**
     * cer苗库管理（无具体种植编号苗库）-分页查询
     *
     * @param plantMultipleStockListPageReqDTO
     * @return
     */
    PageInfo<PlantMultipleStockListPageRspDTO> listPage(PlantMultipleStockListPageReqDTO plantMultipleStockListPageReqDTO);

    /**
     * cer苗库管理（无具体种植编号苗库）-条件查询
     *
     * @param plantMultipleStockQueryListReqDTO
     * @return
     */
    List<PlantMultipleStockQueryListRspDTO> queryList(PlantMultipleStockQueryListReqDTO plantMultipleStockQueryListReqDTO);
}
