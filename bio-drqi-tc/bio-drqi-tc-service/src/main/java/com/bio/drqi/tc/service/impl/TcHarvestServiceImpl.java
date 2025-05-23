package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.service.TcHarvestService;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TcHarvestServiceImpl implements TcHarvestService {


    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;


    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private OssService ossService;

    @Override
    public void createHarvestExcel(TcHarvestCreateHarvestExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectAllByPollinationApplyNum(tcPollinationCreatePollinationExcelReqDTO.getPollinationNum());
        if(CollectionUtil.isEmpty(tcPollinationTbList)){
            throw new BusinessException("无授粉数据");
        }

        List<TcPollinationExcelDTO> tcPollinationExcelDTOList=new ArrayList<>();
        for (TcPollinationTb tcPollinationTb: tcPollinationTbList){
            TcPollinationExcelDTO tcPollinationExcelDTO=new TcPollinationExcelDTO();
            tcPollinationExcelDTO.setMotherRegionNum(tcPollinationTb.getMRegionNum());
            tcPollinationExcelDTO.setMotherSeedNum(tcPollinationTb.getMSeedNum());
            tcPollinationExcelDTO.setMotherSampleCode(tcPollinationTb.getMSampleCode());
            tcPollinationExcelDTO.setMotherBreedName(tcPollinationTb.getMBreedName());
            tcPollinationExcelDTO.setMotherVectorTaskCode(tcPollinationTb.getMVectorTaskCode());
            tcPollinationExcelDTO.setMotherGenerationName(tcPollinationTb.getMGenerationCode());
            tcPollinationExcelDTO.setMotherTcGene(tcPollinationTb.getMTcGene());
            tcPollinationExcelDTO.setFatherRegionNum(tcPollinationTb.getFRegionNum());
            tcPollinationExcelDTO.setFatherSeedNum(tcPollinationTb.getFSeedNum());
            tcPollinationExcelDTO.setFatherSampleCode(tcPollinationTb.getFSampleCode());
            tcPollinationExcelDTO.setFatherBreedName(tcPollinationTb.getFBreedName());
            tcPollinationExcelDTO.setFatherVectorTaskCode(tcPollinationTb.getFVectorTaskCode());
            tcPollinationExcelDTO.setFatherGenerationName(tcPollinationTb.getFGenerationCode());
            tcPollinationExcelDTO.setFatherTcGene(tcPollinationTb.getFTcGene());
            tcPollinationExcelDTO.setPollinationDate(tcPollinationTb.getPollinationDate());
            tcPollinationExcelDTO.setHarvestTypeName(tcPollinationTb.getHarvestTypeName());
            tcPollinationExcelDTOList.add(tcPollinationExcelDTO);
        }
        String excelTemplateName = "田测收获模板表V1.0.xlsx";
        String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
        try {
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, tcPollinationExcelDTOList, TcPollinationExcelDTO.class, httpServletResponse);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }
    }

}
