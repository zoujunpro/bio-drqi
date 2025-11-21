package com.bio.drqi.plant.service;

import com.bio.drqi.plant.req.PlantSingleStockListPageReqDTO;
import com.bio.drqi.plant.req.PlantSingleStockQueryListReqDTO;
import com.bio.drqi.plant.rsp.PlantSingleStockListPageRspDTO;
import com.bio.drqi.plant.rsp.PlantSingleStockQueryListRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface PlantSingleStockService {

    PageInfo<PlantSingleStockListPageRspDTO> listPage(PlantSingleStockListPageReqDTO plantSingleStockListPageReqDTO);

    List<PlantSingleStockQueryListRspDTO> queryList(PlantSingleStockQueryListReqDTO plantSingleStockListPageReqDTO);
}
