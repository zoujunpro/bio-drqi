package com.bio.drqi.manage.service.plant.impl;

import com.bio.drqi.common.enums.PlantStatusEnum;
import com.bio.drqi.domain.PlantMultipleStockTb;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.manage.plant.rsp.PlantBoardCountPlantByVectorTaskEchartsRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantBoardCountRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantBoardPlantStatusEchartsRspDTO;
import com.bio.drqi.manage.service.plant.PlantBoardService;
import com.bio.drqi.mapper.PlantMultipleStockTbMapper;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlantBoardServiceImpl implements PlantBoardService {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Override
    public List<PlantBoardPlantStatusEchartsRspDTO> plantStatusEcharts() {
        List<PlantBoardPlantStatusEchartsRspDTO> plantList = new ArrayList<>();
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectCountGroupByPlantStatus(null, null);
        Map<String, Integer> map = plantSingleStockTbList.stream().collect(Collectors.toMap(PlantSingleStockTb::getPlantStatus, PlantSingleStockTb::getCountNum));
        for (PlantStatusEnum plantStatusEnum : PlantStatusEnum.values()) {
            PlantBoardPlantStatusEchartsRspDTO plantBoardPlantStatusEchartsRspDTO = new PlantBoardPlantStatusEchartsRspDTO();
            plantBoardPlantStatusEchartsRspDTO.setStatus(plantStatusEnum.code);
            plantBoardPlantStatusEchartsRspDTO.setNumber(map.get(plantStatusEnum.code) == null ? 0 : map.get(plantStatusEnum.code));
            plantList.add(plantBoardPlantStatusEchartsRspDTO);
        }
        return plantList;
    }

    @Override
    public List<PlantBoardCountPlantByVectorTaskEchartsRspDTO> CountPlantByVectorTaskEcharts() {
        List<PlantBoardCountPlantByVectorTaskEchartsRspDTO> resultList = new ArrayList<>();
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectCountByVectorTaskCode();
        for (PlantSingleStockTb plantSingleStockTb : plantSingleStockTbList) {
            PlantBoardCountPlantByVectorTaskEchartsRspDTO plantBoardCountPlantByVectorTaskEchartsRspDTO = new PlantBoardCountPlantByVectorTaskEchartsRspDTO();
            plantBoardCountPlantByVectorTaskEchartsRspDTO.setVectorTaskCode(plantSingleStockTb.getVectorTaskCode());
            plantBoardCountPlantByVectorTaskEchartsRspDTO.setCountNumber(plantSingleStockTb.getCountNum());
            resultList.add(plantBoardCountPlantByVectorTaskEchartsRspDTO);

        }
        return resultList;
    }

    @Override
    public PlantBoardCountRspDTO count() {
        Long tempPlantCountNumber = plantMultipleStockTbMapper.selectSumPlantNumber();
        Long tempSampleCountNumber = plantMultipleStockTbMapper.selectSumSampleNumber();
        Long plantCountNumber = plantSingleStockTbMapper.selectCount(null);
        PlantBoardCountRspDTO plantBoardCountRspDTO = new PlantBoardCountRspDTO();
        plantBoardCountRspDTO.setPlantCountNumber(plantCountNumber==null?0:plantCountNumber.intValue());
        plantBoardCountRspDTO.setTempPlantCountNumber(tempPlantCountNumber==null?0:tempPlantCountNumber.intValue());
        plantBoardCountRspDTO.setTempSampleCountNumber(tempSampleCountNumber==null?0:tempSampleCountNumber.intValue());
        plantBoardCountRspDTO.setTempNoSampleCountNumber(plantBoardCountRspDTO.getTempPlantCountNumber()-plantBoardCountRspDTO.getTempSampleCountNumber());
        return plantBoardCountRspDTO;
    }
}
