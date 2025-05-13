package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.TcSampleLayoutTbMapper;
import com.bio.drqi.mapper.TcSampleTestTbMapper;
import com.bio.drqi.tc.SampleUnitDTO;
import com.bio.drqi.tc.req.LayoutConfirmReqDTO;
import com.bio.drqi.tc.req.UploadIdentifyPrimerTemplateReqDTO;
import com.bio.drqi.tc.rsp.LayoutPreviewRspDTO;
import com.bio.drqi.tc.service.TcSampleTestService;
import com.bio.drqi.tc.service.dto.IdentifyPrimerTemplateExcelDTO;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.bio.drqi.tc.util.LayoutUtil;
import com.bio.drqi.tc.util.TcSampleExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcSampleTestServiceImpl implements TcSampleTestService {

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private OssService ossService;

    @Resource
    private TcSampleLayoutTbMapper tcSampleLayoutTbMapper;

    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;

    @Override
    public void downIdentifyPrimerTemplate(HttpServletResponse response, String applyNo) {
        try {
            List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(applyNo);
            List<IdentifyPrimerTemplateExcelDTO> identifyPrimerTemplateExcelDTOList = new ArrayList<IdentifyPrimerTemplateExcelDTO>();
            for (TcSampleTestTb tcSampleTestTb : tcSampleTestTbList) {
                IdentifyPrimerTemplateExcelDTO identifyPrimerTemplateExcelDTO = new IdentifyPrimerTemplateExcelDTO();
                identifyPrimerTemplateExcelDTO.setExperimentCode(tcSampleTestTb.getExperimentCode());
                identifyPrimerTemplateExcelDTO.setRegionNum(tcSampleTestTb.getRegionNum());
                identifyPrimerTemplateExcelDTO.setSeedNum(tcSampleTestTb.getSeedNum());
                identifyPrimerTemplateExcelDTO.setVectorTaskCode(tcSampleTestTb.getVectorTaskCode());
                identifyPrimerTemplateExcelDTO.setSampleCode(tcSampleTestTb.getSampleCode());
                identifyPrimerTemplateExcelDTOList.add(identifyPrimerTemplateExcelDTO);
            }
            String excelTemplateName = "田测鉴定引物填写模板V1.0.xlsx";
            String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, identifyPrimerTemplateExcelDTOList, IdentifyPrimerTemplateExcelDTO.class, response);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("田测鉴定引物填写模板下载失败，请联系管理员检测模板配置");
        }
    }

    @Override
    public void uploadIdentifyPrimerTemplate(UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {

        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(uploadIdentifyPrimerTemplateReqDTO.getApplyNo());

        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + uploadIdentifyPrimerTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, uploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【田测鉴定引物文件】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<IdentifyPrimerTemplateExcelDTO> identifyPrimerTemplateExcelDTOList = ExcelUtil.readExcel(tempFilePath, IdentifyPrimerTemplateExcelDTO.class);

        if (CollectionUtil.isNotEmpty(identifyPrimerTemplateExcelDTOList)) {
            for (IdentifyPrimerTemplateExcelDTO identifyPrimerTemplateExcelDTO : identifyPrimerTemplateExcelDTOList) {
                TcSampleTestTb tcSampleTestTb = tcSampleTestTbMapper.selectOneBySampleApplyNumAndSampleCode(uploadIdentifyPrimerTemplateReqDTO.getApplyNo(), identifyPrimerTemplateExcelDTO.getSampleCode());
                if (tcSampleTestTb != null) {
                    tcSampleTestTb.setIdentifyPrimer(identifyPrimerTemplateExcelDTO.getIdentifyPrimer());
                    tcSampleTestTbMapper.updateIdentifyPrimerById(identifyPrimerTemplateExcelDTO.getIdentifyPrimer(), tcSampleTestTb.getId());
                }
            }
        }

        TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
        tcSampleTestTaskDTO.setIdentifyPrimerTemplateExcelUrl(uploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcSampleTestTaskDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

        //默认排版
        LayoutConfirmReqDTO layoutConfirmReqDTO = getLayoutConfirmReqDTO(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(layoutConfirmReqDTO.getNinetySixList()) || CollectionUtil.isNotEmpty(layoutConfirmReqDTO.getSingleList())) {
            //入库
            layoutConfirm(layoutConfirmReqDTO);
        }
    }

    @Override
    public LayoutPreviewRspDTO layoutPreview(String applyNo) {
        TcSampleLayoutTb tcSampleLayoutTb = tcSampleLayoutTbMapper.selectOneByApplyNo(applyNo);
        if (tcSampleLayoutTb == null) {
            return getLayoutPreviewRspDTO(applyNo);
        } else {
            LayoutPreviewRspDTO layoutPreviewRspDTO = new LayoutPreviewRspDTO();
            layoutPreviewRspDTO.setSingleList(JSONUtil.toList(tcSampleLayoutTb.getSingleContent(), SampleUnitDTO.class));
            JSONArray layoutListJsonArray = JSONUtil.parseArray(tcSampleLayoutTb.getPlateContent());
            List<List<List<SampleUnitDTO>>> ninetySixList = new ArrayList<>();
            //版list
            for (int i = 0; i < layoutListJsonArray.size(); i++) {
                List<List<SampleUnitDTO>> layoutList = new ArrayList<>();
                JSONArray layoutJsonArray = JSONUtil.parseArray(layoutListJsonArray.get(i).toString());
                //遍历版的每一行
                for (int j = 0; j < layoutJsonArray.size(); j++) {
                    List<SampleUnitDTO> rowList = JSONUtil.toList(layoutJsonArray.getJSONArray(j).toString(), SampleUnitDTO.class);
                    layoutList.add(rowList);
                }
                ninetySixList.add(layoutList);
            }
            layoutPreviewRspDTO.setNinetySixList(ninetySixList);
            return layoutPreviewRspDTO;
        }
    }

    @Override
    public void layoutConfirm(LayoutConfirmReqDTO layoutConfirmReqDTO) {
        TcSampleLayoutTb tcSampleLayoutTb = tcSampleLayoutTbMapper.selectOneByApplyNo(layoutConfirmReqDTO.getApplyNo());
        if (tcSampleLayoutTb != null) {
            tcSampleLayoutTb.setSingleContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getSingleList()));
            tcSampleLayoutTb.setPlateContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getNinetySixList()));
            tcSampleLayoutTb.setCreateTime(new Date());
            tcSampleLayoutTbMapper.updateById(tcSampleLayoutTb);
        } else {
            tcSampleLayoutTb = new TcSampleLayoutTb();
            tcSampleLayoutTb.setSingleContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getSingleList()));
            tcSampleLayoutTb.setPlateContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getNinetySixList()));
            tcSampleLayoutTb.setCreateTime(new Date());
            tcSampleLayoutTb.setApplyNo(layoutConfirmReqDTO.getApplyNo());
            tcSampleLayoutTbMapper.insert(tcSampleLayoutTb);
        }
    }

    @Override
    public void dowLayoutExcel(String applyNo, HttpServletResponse httpServletResponse) {
        TcSampleLayoutTb tcSampleLayoutTb = tcSampleLayoutTbMapper.selectOneByApplyNo(applyNo);
        List<List<List<SampleUnitDTO>>> layoutList = null;
        if (tcSampleLayoutTb != null) {
            List<SampleUnitDTO> singleSampleUnitDTOList = JSONUtil.toList(tcSampleLayoutTb.getSingleContent(), SampleUnitDTO.class);
            JSONArray layoutListJsonArray = JSONUtil.parseArray(tcSampleLayoutTb.getPlateContent());
            if (!layoutListJsonArray.isEmpty()) {
                layoutList = new ArrayList<>();
                for (int i = 0; i < layoutListJsonArray.size(); i++) {
                    List<List<SampleUnitDTO>> layout = new ArrayList<>();
                    //一个96孔板
                    JSONArray layoutJsonArray = JSONUtil.parseArray(layoutListJsonArray.get(i).toString());
                    for (int j = 0; j < layoutJsonArray.size(); j++) {
                        //一个96孔板代表一行
                        List<SampleUnitDTO> rowList = JSONUtil.toList(layoutJsonArray.getJSONArray(j).toString(), SampleUnitDTO.class);
                        layout.add(rowList);
                    }
                    layoutList.add(layout);
                }

            }
            TcSampleExcelUtil.createExcel(applyNo, layoutList, singleSampleUnitDTOList, httpServletResponse, "取样标签排版.xlsx");
        } else {
            //默认排版
            LayoutConfirmReqDTO layoutConfirmReqDTO = getLayoutConfirmReqDTO(applyNo);
            if (CollectionUtil.isNotEmpty(layoutConfirmReqDTO.getNinetySixList()) || CollectionUtil.isNotEmpty(layoutConfirmReqDTO.getSingleList())) {
                //入库
                layoutConfirm(layoutConfirmReqDTO);
                //再次下载
                dowLayoutExcel(applyNo, httpServletResponse);
            }

        }
    }


    private LayoutConfirmReqDTO getLayoutConfirmReqDTO(String applyNo) {
        LayoutConfirmReqDTO layoutConfirmReqDTO = new LayoutConfirmReqDTO();
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(applyNo);
        if (CollectionUtil.isEmpty(tcSampleTestTbList)) {
            return layoutConfirmReqDTO;
        }
        List<TcSampleTestTb> noIdentifyPrimerList = tcSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noIdentifyPrimerList)) {
            noIdentifyPrimerList.forEach(tcSampleTestTb -> {
                layoutConfirmReqDTO.fillSampleToSingleList(tcSampleTestTb.getVectorTaskCode(), tcSampleTestTb.getExperimentCode(), tcSampleTestTb.getRegionNum(), tcSampleTestTb.getSeedNum(),tcSampleTestTb.getSampleCode(),tcSampleTestTb.getIdentifyPrimer());
            });
        }
        //96孔板
        List<TcSampleTestTb> identifyPrimerList = tcSampleTestTbList.stream().filter(tcSampleTestTb -> StringUtils.isNotEmpty(tcSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(identifyPrimerList)) {
            layoutConfirmReqDTO.setNinetySixList(LayoutUtil.fillSampleToNinetySixList(identifyPrimerList));
        }
        layoutConfirmReqDTO.setApplyNo(applyNo);
        return layoutConfirmReqDTO;
    }


    private LayoutPreviewRspDTO getLayoutPreviewRspDTO(String applyNo) {
        LayoutPreviewRspDTO layoutPreviewRspDTO = new LayoutPreviewRspDTO();
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(applyNo);
        if (CollectionUtil.isEmpty(tcSampleTestTbList)) {
            return layoutPreviewRspDTO;
        }
        List<TcSampleTestTb> noIdentifyPrimerList = tcSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noIdentifyPrimerList)) {
            noIdentifyPrimerList.forEach(tcSampleTestTb -> {
                layoutPreviewRspDTO.fillSampleToSingleList(tcSampleTestTb.getVectorTaskCode(), tcSampleTestTb.getExperimentCode(), tcSampleTestTb.getRegionNum(), tcSampleTestTb.getSeedNum(),tcSampleTestTb.getSampleCode(),tcSampleTestTb.getIdentifyPrimer());
            });
        }
        //96孔板
        List<TcSampleTestTb> identifyPrimerList = tcSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isNotEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(identifyPrimerList)) {
            layoutPreviewRspDTO.setNinetySixList(LayoutUtil.fillSampleToNinetySixList(identifyPrimerList));
        }

        return layoutPreviewRspDTO;
    }
}
