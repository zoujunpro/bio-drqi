package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcPollinationApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcPollinationApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.mapper.TcSampleTestTbMapper;
import com.bio.drqi.tc.enums.PollinationParentFlagEnum;
import com.bio.drqi.tc.enums.SampleTestCheckResultEnum;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
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
import java.util.UUID;
import java.util.stream.Collectors;

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
        List<TcPollinationExcelDTO> tcPollinationOneExcelDTOList = new ArrayList<TcPollinationExcelDTO>();
        for (TcPollinationCreatePollinationExcelReqDTO.Content content : tcPollinationCreatePollinationExcelReqDTO.getContentList()) {
            TcExperimentDesignTb tcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum(), content.getRegionNum(), content.getSeedNum());
            if (tcExperimentDesignTb == null) {
                throw new BusinessException("数据异常，找不到此试验设计种子信息 试验：" + tcPollinationCreatePollinationExcelReqDTO.getExperimentNum() + "种子号：" + content.getSeedNum() + "区域：" + content.getRegionNum());
            }
            //没有单株编号
            if (BioDrQiContents.Y.equals(content.getSinglePlantFlag())) {
                if (PollinationParentFlagEnum.father.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, null);
                    tcPollinationOneExcelDTOList.add(tcPollinationExcelDTO);

                } else if (PollinationParentFlagEnum.mother.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, null);
                    tcPollinationOneExcelDTOList.add(tcPollinationExcelDTO);
                } else if (PollinationParentFlagEnum.parent.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO fatherTcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, null);
                    tcPollinationOneExcelDTOList.add(fatherTcPollinationExcelDTO);
                    TcPollinationExcelDTO matherTcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, null);
                    tcPollinationOneExcelDTOList.add(matherTcPollinationExcelDTO);
                }
            } else {
                List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNumAndSeedNumAndRegionNumAndCheckResult(tcPollinationCreatePollinationExcelReqDTO.getSampleApplyNum(), content.getSeedNum(), content.getRegionNum(), SampleTestCheckResultEnum.stay.name());
                List<String> sampleCodeList = tcSampleTestTbList.stream().map(TcSampleTestTb::getSampleCode).distinct().collect(Collectors.toList());
                for (int i = 0; i < content.getSinglePlantNumber(); i++) {
                    String sampleCode=null;
                    if(i<sampleCodeList.size()){
                        sampleCode=sampleCodeList.get(i);
                    }else {
                        //todo 生成单株编号
                        sampleCode= IdUtils.simpleUUID();
                    }
                    if (PollinationParentFlagEnum.father.name().equals(content.getParentFlag())) {

                        TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, sampleCode);
                        tcPollinationOneExcelDTOList.add(tcPollinationExcelDTO);
                    } else if (PollinationParentFlagEnum.mother.name().equals(content.getParentFlag())) {
                        TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, sampleCode);
                        tcPollinationOneExcelDTOList.add(tcPollinationExcelDTO);
                    } else if (PollinationParentFlagEnum.parent.name().equals(content.getParentFlag())) {
                        TcPollinationExcelDTO fatherTcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, sampleCode);
                        tcPollinationOneExcelDTOList.add(fatherTcPollinationExcelDTO);
                        TcPollinationExcelDTO matherTcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, sampleCode);
                        tcPollinationOneExcelDTOList.add(matherTcPollinationExcelDTO);
                    }
                }
            }
        }
        String excelTemplateName = "田测授粉数据表单模板V1.0.xlsx";
        String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
        try {
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, tcPollinationOneExcelDTOList, TcPollinationExcelDTO.class, httpServletResponse);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }
    }
}
