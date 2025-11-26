package com.bio.drqi.manage.service.plant;


import com.bio.drqi.manage.plant.req.PlantSingleStockListPageReqDTO;
import com.bio.drqi.manage.plant.req.PlantSingleStockQueryListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockListPageRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockQueryListRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface PlantSingleStockService {

    PageInfo<PlantSingleStockListPageRspDTO> listPage(PlantSingleStockListPageReqDTO plantSingleStockListPageReqDTO);

    List<PlantSingleStockQueryListRspDTO> queryList(PlantSingleStockQueryListReqDTO plantSingleStockListPageReqDTO);


}
