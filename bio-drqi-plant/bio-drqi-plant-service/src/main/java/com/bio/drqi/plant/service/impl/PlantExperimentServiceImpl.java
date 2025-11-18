package com.bio.drqi.plant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.PlantExperimentDetailTb;
import com.bio.drqi.domain.PlantExperimentTb;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.PlantExperimentDetailTbMapper;
import com.bio.drqi.mapper.PlantExperimentTbMapper;
import com.bio.drqi.plant.dto.ExperimentExcelDTO;
import com.bio.drqi.plant.req.PlantExperimentListPageDetailReqDTO;
import com.bio.drqi.plant.req.PlantExperimentListPageReqDTO;
import com.bio.drqi.plant.rsp.PlantExperimentListPageDetailRspDTO;
import com.bio.drqi.plant.rsp.PlantExperimentListPageRspDTO;
import com.bio.drqi.plant.service.PlantExperimentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlantExperimentServiceImpl implements PlantExperimentService {

    @Resource
    private PlantExperimentTbMapper plantExperimentTbMapper;

    @Resource
    private PlantExperimentDetailTbMapper plantExperimentDetailTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public PageInfo<PlantExperimentListPageRspDTO> listPage(PlantExperimentListPageReqDTO plantExperimentListPageReqDTO) {
        PageHelper.startPage(plantExperimentListPageReqDTO.getPageNum(), plantExperimentListPageReqDTO.getPageSize());
        PlantExperimentTb plantExperimentTb = BeanUtils.copyProperties(plantExperimentListPageReqDTO, PlantExperimentTb.class);
        plantExperimentTb.setVectorTaskCodes(plantExperimentListPageReqDTO.getVectorTaskCode());
        plantExperimentTb.setPdNums(plantExperimentListPageReqDTO.getPdNum());
        List<PlantExperimentTb> plantExperimentTbList = plantExperimentTbMapper.selectSelective(plantExperimentTb);
        PageInfo<PlantExperimentTb> srcPageInfo = new PageInfo<>(plantExperimentTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, PlantExperimentListPageRspDTO.class);
    }

    @Override
    public PageInfo<PlantExperimentListPageDetailRspDTO> listPageDetail(PlantExperimentListPageDetailReqDTO plantExperimentListPageDetailReqDTO) {
        Map<String, String> cerSpeciesConfMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        PageHelper.startPage(plantExperimentListPageDetailReqDTO.getPageNum(), plantExperimentListPageDetailReqDTO.getPageSize());
        List<PlantExperimentDetailTb> plantExperimentDetailTbList = plantExperimentDetailTbMapper.selectSelective(BeanUtils.copyProperties(plantExperimentListPageDetailReqDTO, PlantExperimentDetailTb.class));
        PageInfo<PlantExperimentDetailTb> srcPageInfo = new PageInfo<>(plantExperimentDetailTbList);
        PageInfo<PlantExperimentListPageDetailRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, PlantExperimentListPageDetailRspDTO.class);
        if(CollectionUtil.isNotEmpty(resultPageInfo.getList())){
            resultPageInfo.getList().forEach(plantExperimentListPageDetailRspDTO -> {
                plantExperimentListPageDetailRspDTO.setBreedName(cerBreedDictMap.get(plantExperimentListPageDetailRspDTO.getBreedCode()));
                plantExperimentListPageDetailRspDTO.setSpeciesName(cerSpeciesConfMap.get(plantExperimentListPageDetailRspDTO.getSpeciesCode()));
            });
        }
        return resultPageInfo;
    }

    @Override
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        ExcelUtil.writeExcel("CER试验模板文件", "sheet1", new ArrayList<>(), ExperimentExcelDTO.class, httpServletResponse);
    }
}
