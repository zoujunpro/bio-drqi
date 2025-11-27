package com.bio.drqi.manage.service.plant.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.PlantApplyDetailTb;
import com.bio.drqi.domain.PlantApplyTb;
import com.bio.drqi.manage.dto.plant.ExperimentExcelDTO;
import com.bio.drqi.manage.plant.req.PlantApplyListPageDetailReqDTO;
import com.bio.drqi.manage.plant.req.PlantApplyListPageReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantApplyListPageDetailRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantApplyListPageRspDTO;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.PlantApplyDetailTbMapper;
import com.bio.drqi.mapper.PlantApplyTbMapper;

import com.bio.drqi.manage.service.plant.PlantApplyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlantApplyServiceImpl implements PlantApplyService {

    @Resource
    private PlantApplyTbMapper plantApplyTbMapper;

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public PageInfo<PlantApplyListPageRspDTO> listPage(PlantApplyListPageReqDTO plantApplyListPageReqDTO) {
        PageHelper.startPage(plantApplyListPageReqDTO.getPageNum(), plantApplyListPageReqDTO.getPageSize());
        PlantApplyTb plantApplyTb = BeanUtils.copyProperties(plantApplyListPageReqDTO, PlantApplyTb.class);
        plantApplyTb.setVectorTaskCodes(plantApplyListPageReqDTO.getVectorTaskCode());
        plantApplyTb.setPdNums(plantApplyListPageReqDTO.getPdNum());
        List<PlantApplyTb> plantApplyTbList = plantApplyTbMapper.selectSelective(plantApplyTb);
        PageInfo<PlantApplyTb> srcPageInfo = new PageInfo<>(plantApplyTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, PlantApplyListPageRspDTO.class);
    }

    @Override
    public PageInfo<PlantApplyListPageDetailRspDTO> listPageDetail(PlantApplyListPageDetailReqDTO plantApplyListPageDetailReqDTO) {
        Map<String, String> cerSpeciesConfMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        PageHelper.startPage(plantApplyListPageDetailReqDTO.getPageNum(), plantApplyListPageDetailReqDTO.getPageSize());
        List<PlantApplyDetailTb> plantExperimentDetailTbList = plantApplyDetailTbMapper.selectSelective(BeanUtils.copyProperties(plantApplyListPageDetailReqDTO, PlantApplyDetailTb.class));
        PageInfo<PlantApplyDetailTb> srcPageInfo = new PageInfo<>(plantExperimentDetailTbList);
        PageInfo<PlantApplyListPageDetailRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, PlantApplyListPageDetailRspDTO.class);
        if(CollectionUtil.isNotEmpty(resultPageInfo.getList())){
            resultPageInfo.getList().forEach(plantApplyListPageDetailRspDTO -> {
                plantApplyListPageDetailRspDTO.setBreedName(cerBreedDictMap.get(plantApplyListPageDetailRspDTO.getBreedCode()));
                plantApplyListPageDetailRspDTO.setSpeciesName(cerSpeciesConfMap.get(plantApplyListPageDetailRspDTO.getSpeciesCode()));
            });
        }
        return resultPageInfo;
    }

    @Override
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        ExcelUtil.writeExcel("CER试验模板文件", "sheet1", new ArrayList<>(), ExperimentExcelDTO.class, httpServletResponse);
    }
}
