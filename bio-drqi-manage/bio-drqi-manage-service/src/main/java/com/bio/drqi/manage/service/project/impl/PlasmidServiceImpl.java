package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.PlasmidDTO;
import com.bio.drqi.manage.service.project.PlasmidService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.manage.plasmid.req.QueryPagePlasmidReqDTO;
import com.bio.drqi.manage.plasmid.rsp.QueryPagePlasmidRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PlasmidServiceImpl implements PlasmidService {

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Resource
    private OssService ossService;

    @Override
    public List<QueryPagePlasmidRspDTO> listByVectorTask(QueryPagePlasmidReqDTO queryPagePlasmidReqDTO) {
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskId(queryPagePlasmidReqDTO.getVectorTaskId());
        return BeanUtils.copyListProperties(cerPlasmidQualityTbList, QueryPagePlasmidRspDTO.class);
    }


    @Override
    public void downPlasmidCheckTemplate(String vectorTaskCode, HttpServletResponse response) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            throw new BusinessException("不存在此载体任务");
        }
        List<PlasmidDTO.Content> plasmidCheckExcelDTOList = new ArrayList<>();

        List<CerVectorGroupTb> cerVectorGroupTbList = cerVectorGroupTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if(CollectionUtil.isNotEmpty(cerVectorTbList)){
            for (CerVectorTb cerVectorTb:cerVectorTbList){
                PlasmidDTO.Content content = new PlasmidDTO.Content();
                content.setPlasmidName(cerVectorTb.getPlasmidName());
                content.setQualityInspectionType("质粒制备");
                plasmidCheckExcelDTOList.add(content);
            }
        }
        if(CollectionUtil.isNotEmpty(cerVectorGroupTbList)){
            for (CerVectorGroupTb cerVectorGroupTb:cerVectorGroupTbList){
                PlasmidDTO.Content content = new PlasmidDTO.Content();
                content.setPlasmidName(cerVectorGroupTb.getGroupName());
                content.setQualityInspectionType("农杆菌检测");
                plasmidCheckExcelDTOList.add(content);
            }
        }

        String excelTemplateName = "测试质粒质检模板V1.0.xlsx";
        String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
        try {
            ossService.downloadPath(templateDir, "template", excelTemplateName);

            ExcelUtil.fillExcel(templateDir, plasmidCheckExcelDTOList, PlasmidDTO.Content.class, response);
        } catch (Exception e) {
            log.error("质粒质检模板下载失败,", e);
            throw new BusinessException("模板下载失败");
        }
    }

}
