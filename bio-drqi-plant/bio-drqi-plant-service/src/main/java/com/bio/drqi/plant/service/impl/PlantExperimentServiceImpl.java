package com.bio.drqi.plant.service.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.domain.PlantExperimentDetailTb;
import com.bio.drqi.domain.PlantExperimentTb;
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

@Service
public class PlantExperimentServiceImpl implements PlantExperimentService {

    @Resource
    private PlantExperimentTbMapper plantExperimentTbMapper;

    private PlantExperimentDetailTbMapper plantExperimentDetailTbMapper;

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
        PageHelper.startPage(plantExperimentListPageDetailReqDTO.getPageNum(), plantExperimentListPageDetailReqDTO.getPageSize());
        List<PlantExperimentDetailTb> plantExperimentDetailTbList = plantExperimentDetailTbMapper.selectSelective(BeanUtils.copyProperties(plantExperimentListPageDetailReqDTO, PlantExperimentDetailTb.class));
        PageInfo<PlantExperimentDetailTb> srcPageInfo=new PageInfo<>(plantExperimentDetailTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, PlantExperimentListPageDetailRspDTO.class);
    }

    @Override
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        ExcelUtil.writeExcel("CER试验模板文件", "sheet1", new ArrayList<>(), ExperimentExcelDTO.class, httpServletResponse);
    }
}
