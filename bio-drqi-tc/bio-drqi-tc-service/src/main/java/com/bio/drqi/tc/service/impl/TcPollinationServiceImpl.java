package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcPollinationApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcPollinationApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.mapper.TcSampleTestTbMapper;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.bio.drqi.tc.service.dto.TcPollinationOneExcelDTO;
import com.bio.drqi.tc.service.dto.TcTestExcelDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class TcPollinationServiceImpl implements TcPollinationService {


    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;

    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private OssService ossService;

    @Override
    public PageInfo<TcPollinationListPageRspDTO> listPage(TcPollinationListPageReqDTO tcPollinationListPageReqDTO) {
        PageHelper.startPage(tcPollinationListPageReqDTO.getPageNum(), tcPollinationListPageReqDTO.getPageSize());
        List<TcPollinationApplyTb> tcPollinationApplyTbList = tcPollinationApplyTbMapper.selectSelective(BeanUtils.copyProperties(tcPollinationListPageReqDTO, TcPollinationApplyTb.class));
        PageInfo<TcPollinationApplyTb> srcPageInfo = new PageInfo<>(tcPollinationApplyTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcPollinationListPageRspDTO.class);
    }

    @Override
    public PageInfo<TcPollinationListPageDetailRspDTO> listPageDetail(TcPollinationListPageDetailReqDTO tcPollinationListPageDetailReqDTO) {
        PageHelper.startPage(tcPollinationListPageDetailReqDTO.getPageNum(), tcPollinationListPageDetailReqDTO.getPageSize());
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectSelective(BeanUtils.copyProperties(tcPollinationListPageDetailReqDTO, TcPollinationTb.class));
        PageInfo<TcPollinationTb> srcPageInfo = new PageInfo<>(tcPollinationTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcPollinationListPageDetailRspDTO.class);
    }

    @Override
    public void createPollinationExcel(TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<TcPollinationOneExcelDTO> tcPollinationOneExcelDTOList = new ArrayList<TcPollinationOneExcelDTO>();
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentCode(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum());
        for (TcExperimentDesignTb tcExperimentDesignTb : tcExperimentDesignTbList) {
            TcPollinationOneExcelDTO tcPollinationOneExcelDTO = new TcPollinationOneExcelDTO();
            tcPollinationOneExcelDTO.setExperimentNum(tcExperimentDesignTb.getExperimentNum());
            tcPollinationOneExcelDTO.setRegionNum(tcExperimentDesignTb.getRegionNum());
            tcPollinationOneExcelDTO.setSeedNum(tcExperimentDesignTb.getSeedNum());
            tcPollinationOneExcelDTO.setBreedName(tcExperimentDesignTb.getBreedName());
            tcPollinationOneExcelDTO.setVectorTaskCode(tcExperimentDesignTb.getVectorTaskCode());
            tcPollinationOneExcelDTO.setGenerationCode(tcExperimentDesignTb.getGenerationCode());
            tcPollinationOneExcelDTO.setTcGene(tcExperimentDesignTb.getTcGene());
            tcPollinationOneExcelDTO.setSampleApplyNum(tcPollinationCreatePollinationExcelReqDTO.getSampleApplyNum());
            if (StringUtils.isNotEmpty(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum())) {
                List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNumAndRegionNumAndSeedNum(tcPollinationCreatePollinationExcelReqDTO.getSampleApplyNum(), tcExperimentDesignTb.getRegionNum(), tcExperimentDesignTb.getSeedNum());
                tcPollinationOneExcelDTO.setSampleNumber(CollectionUtil.isEmpty(tcSampleTestTbList) ? 0 : tcSampleTestTbList.size());
            }
            tcPollinationOneExcelDTOList.add(tcPollinationOneExcelDTO);
        }
        try {
            String excelTemplateName = "田测授粉表单模板V1.0.xlsx";
            String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, tcPollinationOneExcelDTOList, TcPollinationOneExcelDTO.class, httpServletResponse);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }
    }
}
