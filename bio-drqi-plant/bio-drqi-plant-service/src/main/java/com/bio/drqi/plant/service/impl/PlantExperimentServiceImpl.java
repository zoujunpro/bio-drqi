package com.bio.drqi.plant.service.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.domain.PlantExperimentDetailTb;
import com.bio.drqi.domain.PlantExperimentTb;
import com.bio.drqi.mapper.PlantExperimentTbMapper;
import com.bio.drqi.plant.dto.ExperimentExcelDTO;
import com.bio.drqi.plant.req.PlantExperimentReqDTO;
import com.bio.drqi.plant.rsp.PlantExperimentRspDTO;
import com.bio.drqi.plant.service.PlantExperimentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlantExperimentServiceImpl implements PlantExperimentService {

    @Resource
    private PlantExperimentTbMapper plantExperimentTbMapper;

    @Override
    public PageInfo<PlantExperimentRspDTO> listPage(PlantExperimentReqDTO plantExperimentReqDTO) {
        PageHelper.startPage(plantExperimentReqDTO.getPageNum(), plantExperimentReqDTO.getPageSize());
        PlantExperimentTb plantExperimentTb = BeanUtils.copyProperties(plantExperimentReqDTO, PlantExperimentTb.class);
        plantExperimentTb.setVectorTaskCodes(plantExperimentReqDTO.getVectorTaskCode());
        plantExperimentTb.setPdNums(plantExperimentReqDTO.getPdNum());
        List<PlantExperimentTb> plantExperimentTbList = plantExperimentTbMapper.selectSelective(plantExperimentTb);
        PageInfo<PlantExperimentTb> srcPageInfo = new PageInfo<>(plantExperimentTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, PlantExperimentRspDTO.class);
    }

    @Override
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        ExcelUtil.writeExcel("CER试验模板文件", "sheet1", new ArrayList<>(), ExperimentExcelDTO.class, httpServletResponse);
    }
}
