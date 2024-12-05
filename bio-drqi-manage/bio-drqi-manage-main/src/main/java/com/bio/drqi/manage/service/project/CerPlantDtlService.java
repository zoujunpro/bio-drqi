package com.bio.drqi.manage.service.project;

import com.bio.drqi.plant.req.PlantDtlListReqDTO;
import com.bio.drqi.plant.rsp.PlantDtlListRspDTO;
import com.github.pagehelper.PageInfo;

public interface CerPlantDtlService {

    PageInfo<PlantDtlListRspDTO> listPage(PlantDtlListReqDTO plantDtlListReqDTO);
}
