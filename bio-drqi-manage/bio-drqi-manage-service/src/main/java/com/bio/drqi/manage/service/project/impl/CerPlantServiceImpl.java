package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.CerPlantFixedFieldEnum;
import com.bio.drqi.manage.service.project.CerPlantService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.manage.plant.req.DownloadTemplateReqDTO;
import com.bio.drqi.manage.plant.req.PlantListPageReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDetailRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantListPageRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ExcelHead;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CerPlantServiceImpl implements CerPlantService {

    @Resource
    private CerPlantTbMapper cerPlantTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;


    @Resource
    private CerSpeciesPlantFeaturesConfMapper cerSpeciesPlantFeaturesConfMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;


    @Override
    public PageInfo<PlantListPageRspDTO> listPage(PlantListPageReqDTO plantListPageReqDTO) {
        BeanUtils.trimFiledSpace(plantListPageReqDTO);
        PageHelper.startPage(plantListPageReqDTO.getPageNum(), plantListPageReqDTO.getPageSize());
        List<CerPlantTb> cerPlantTbList = cerPlantTbMapper.selectAllByProjectId(plantListPageReqDTO.getProjectId());
        PageInfo<CerPlantTb> srcPage = new PageInfo<>(cerPlantTbList);
        return BeanUtils.copyPageInfoProperties(srcPage, PlantListPageRspDTO.class);
    }

    @Override
    public PlantDetailRspDTO detail(Integer id) {
        CerPlantTb cerPlantTb = cerPlantTbMapper.selectById(id);
        PlantDetailRspDTO plantDetailRspDTO = new PlantDetailRspDTO();
        BeanUtils.copyProperties(cerPlantTb, plantDetailRspDTO);
        return plantDetailRspDTO;
    }


    @Override
    public void downloadTemplate(DownloadTemplateReqDTO downloadTemplateReqDTO, HttpServletResponse httpServletResponse) {
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectById(downloadTemplateReqDTO.getSubProjectId());
        if (cerSubProjectTb == null) {
            throw new BusinessException("不存在此子项目信息");
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(downloadTemplateReqDTO.getVectorTaskId());

        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(cerVectorTaskTb.getSpeciesCode());
        if (cerSpeciesConf == null) {
            throw new BusinessException("不支持该受体材料：" + cerSubProjectTb.getSpeciesCode());
        }
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        List<Map<String, Object>> excelData = new ArrayList<>();
        for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
            Map<String, Object> excelMap = new HashMap<>();
            excelMap.put("sampleCode", cerSampleTestTb.getSampleCode());
            excelMap.put("checkResult", cerSampleTestTb.getCheckResult());
            excelMap.put("editType", cerSampleTestTb.getTestEditType());
            excelMap.put("acceptorMaterial", cerSampleTestTb.getAcceptorMaterial());
            excelMap.put("species", cerSpeciesConf.getSpeciesName());
            excelMap.put("generation", "T0");
            excelData.add(excelMap);
        }

        ExcelUtil.write(httpServletResponse, getExcelHeads(cerSpeciesConf.getSpeciesCode()), excelData, "CER种植数据上传模板.xlsx");
    }

    @Override
    public List<Map<String, String>> fieldList(String speciesCode) {
        List<Map<String, String>> mapListResult = new ArrayList<>();
        List<CerSpeciesPlantFeaturesConf> cerSpeciesPlantFeaturesConfList = cerSpeciesPlantFeaturesConfMapper.selectAllBySpeciesCodeOrderByOrderNum(speciesCode);
        for (CerSpeciesPlantFeaturesConf cerSpeciesPlantFeaturesConf : cerSpeciesPlantFeaturesConfList) {
            Map<String, String> map = new HashMap<>();
            map.put(cerSpeciesPlantFeaturesConf.getPlantFeaturesName(), cerSpeciesPlantFeaturesConf.getPlantFeaturesDesc());
            mapListResult.add(map);
        }
        return mapListResult;
    }

    @NotNull
    private List<ExcelHead> getExcelHeads(String species) {
        List<ExcelHead> headList = CerPlantFixedFieldEnum.getFixedField().stream().map(map -> new ExcelHead(map.keySet().iterator().next(), map.values().iterator().next())).collect(Collectors.toList());
        List<CerSpeciesPlantFeaturesConf> cerSpeciesPlantFeaturesConfList = cerSpeciesPlantFeaturesConfMapper.selectAllBySpeciesCodeOrderByOrderNum(species);
        if (CollectionUtil.isNotEmpty(cerSpeciesPlantFeaturesConfList)) {
            cerSpeciesPlantFeaturesConfList.forEach(cerSpeciesPlantFeaturesConf -> {
                headList.add(new ExcelHead(cerSpeciesPlantFeaturesConf.getPlantFeaturesName(), cerSpeciesPlantFeaturesConf.getPlantFeaturesDesc()));
            });
        }
        return headList;
    }

}
