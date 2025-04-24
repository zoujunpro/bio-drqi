package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.plant.req.PlantDtlListDetailReqDTO;
import com.bio.drqi.manage.plant.req.PlantDtlListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListDetailRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

public interface CerPlantDtlService {

    PageInfo<PlantDtlListRspDTO> listPage(PlantDtlListReqDTO plantDtlListReqDTO);

    PageInfo<PlantDtlListDetailRspDTO> listDetail(PlantDtlListDetailReqDTO plantDtlListReqDTO);
}
