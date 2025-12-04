package com.bio.drqi.manage.service.plant;

import com.bio.drqi.manage.plant.rsp.PlantBoardCountPlantByVectorTaskEchartsRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantBoardPlantStatusEchartsRspDTO;

import java.util.List;

public interface PlantBoardService {

    List<PlantBoardPlantStatusEchartsRspDTO> plantStatusEcharts();

    List<PlantBoardCountPlantByVectorTaskEchartsRspDTO> CountPlantByVectorTaskEcharts();
}
