package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.TcSampleLayoutTbMapper;
import com.bio.drqi.mapper.TcSampleTestApplyTbMapper;
import com.bio.drqi.mapper.TcSampleTestTbMapper;
import com.bio.drqi.tc.SampleUnitDTO;
import com.bio.drqi.tc.enums.SampleTestApplyTypeEnum;
import com.bio.drqi.tc.req.*;
import com.bio.drqi.tc.rsp.*;
import com.bio.drqi.tc.service.TcSampleTestService;
import com.bio.drqi.tc.service.dto.IdentifyPrimerTemplateExcelDTO;
import com.bio.drqi.tc.service.dto.TcTestExcelDTO;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.bio.drqi.tc.util.LayoutUtil;
import com.bio.drqi.tc.util.TcSampleExcelUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcSampleTestServiceImpl implements TcSampleTestService {

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcSampleTestApplyTbMapper tcSampleTestApplyTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private OssService ossService;

    @Resource
    private TcSampleLayoutTbMapper tcSampleLayoutTbMapper;

    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;

    @Override
    public PageInfo<TcSampleTestListPageRspDTO> listPage(TcSampleTestListPageReqDTO tcSampleTestListPageReqDTO) {
        PageHelper.startPage(tcSampleTestListPageReqDTO.getPageNum(), tcSampleTestListPageReqDTO.getPageSize());
        List<TcSampleTestApplyTb> tcSampleTestApplyTbList = tcSampleTestApplyTbMapper.selectSelective(BeanUtils.copyProperties(tcSampleTestListPageReqDTO, TcSampleTestApplyTb.class));
        PageInfo<TcSampleTestApplyTb> srcPageInfo = new PageInfo<>(tcSampleTestApplyTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcSampleTestListPageRspDTO.class);
    }

    @Override
    public PageInfo<TcSampleTestListPageDetailRspDTO> listPageDetail(TcSampleTestListPageDetailReqDTO tcSampleTestListPageDetailReqDTO) {
        PageHelper.startPage(tcSampleTestListPageDetailReqDTO.getPageNum(), tcSampleTestListPageDetailReqDTO.getPageSize());
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectSelective(BeanUtils.copyProperties(tcSampleTestListPageDetailReqDTO, TcSampleTestTb.class));
        PageInfo<TcSampleTestTb> srcPageInfo = new PageInfo<>(tcSampleTestTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcSampleTestListPageDetailRspDTO.class);
    }

    @Override
    public List<String> listByExperimentNum(String experimentNum) {
        List<TcSampleTestApplyTb> tcSampleTestApplyTbList = tcSampleTestApplyTbMapper.selectAllByExperimentNum(experimentNum);
        if (CollectionUtil.isNotEmpty(tcSampleTestApplyTbList)) {
            return tcSampleTestApplyTbList.stream().map(TcSampleTestApplyTb::getSampleApplyNum).collect(Collectors.toList());
        }
        return null;
    }


    @Override
    public void downTestTemplate(TcSampleTestDownTestTemplateReqDTO tcSampleTestDownTestTemplateReqDTO, HttpServletResponse response) {
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(tcSampleTestDownTestTemplateReqDTO.getApplyNo());
        List<TcTestExcelDTO> testExcelDTOList = new ArrayList<>();
        for (TcSampleTestTb tcSampleTestTb : tcSampleTestTbList) {
            TcTestExcelDTO tcTestExcelDTO = new TcTestExcelDTO();
            tcTestExcelDTO.setExperimentNum(tcSampleTestTb.getExperimentNum());
            tcTestExcelDTO.setRegionNum(tcSampleTestTb.getRegionNum());
            tcTestExcelDTO.setSeedNum(tcSampleTestTb.getSeedNum());
            tcTestExcelDTO.setSampleCode(tcSampleTestTb.getSampleCode());
            tcTestExcelDTO.setSampleTime(tcSampleTestTb.getSampleTime());
            tcTestExcelDTO.setGeneration(tcSampleTestTb.getGenerationCode());
            tcTestExcelDTO.setVectorTaskCode(tcSampleTestTb.getVectorTaskCode());
            testExcelDTOList.add(tcTestExcelDTO);
        }
        try {
            String excelTemplateName = "田测检测数据上传模板_V1.xlsx";
            String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, testExcelDTOList, TcTestExcelDTO.class, response);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }
    }

    @Override
    public void uploadTestTemplate(TcSampleTestUploadTestTemplateReqDTO tcSampleTestUploadTestTemplateReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcSampleTestUploadTestTemplateReqDTO.getApplyNo());

        TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);


        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行中任务");
        }
        if (!tcSampleTestUploadTestTemplateReqDTO.getExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcSampleTestUploadTestTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, tcSampleTestUploadTestTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<TcTestExcelDTO> tcTestExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcTestExcelDTO.class);
        if (CollectionUtil.isEmpty(tcTestExcelDTOList)) {
            throw new BusinessException("无数据提交");
        }
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNumAndSampleCodeIn(bioTaskDtlTb.getTaskNum(), tcTestExcelDTOList.stream().map(TcTestExcelDTO::getSampleCode).collect(Collectors.toList()));
        if (tcTestExcelDTOList.size() != tcSampleTestTbList.size()) {
            throw new BusinessException("有取样编号不存在此申请中");
        }
        List<TcSampleTestTb> updateList = new ArrayList<TcSampleTestTb>();
        Map<String, TcSampleTestTb> tcSampleTestTbMap = tcSampleTestTbList.stream().collect(Collectors.toMap(TcSampleTestTb::getSampleCode, tcSampleTestTb -> tcSampleTestTb));
        for (TcTestExcelDTO tcTestExcelDTO : tcTestExcelDTOList) {
            log.info("检测数据上送 数据处理中：" + tcTestExcelDTO.getSampleCode());
            TcSampleTestTb tcSampleTestTb = tcSampleTestTbMap.get(tcTestExcelDTO.getSampleCode());
            TcSampleTestTb updateCerSampleTestTb = new TcSampleTestTb();
            updateCerSampleTestTb.setId(tcSampleTestTb.getId());
            updateCerSampleTestTb.setTestIdentifyPrimer(tcTestExcelDTO.getIdentifyPrimer());
            updateCerSampleTestTb.setTestMethod(tcTestExcelDTO.getTestMethod());
            updateCerSampleTestTb.setTestEditType(tcTestExcelDTO.getEditType());
            updateCerSampleTestTb.setTestNoTransIdentityPrimer(tcTestExcelDTO.getNoTransIdentityPrimer());
            updateCerSampleTestTb.setTestIsGeneModifyPositive(tcTestExcelDTO.getIsGeneModifyPositive());
            updateCerSampleTestTb.setTestIfFixedPoint(tcTestExcelDTO.getIfFixedPoint());
            updateCerSampleTestTb.setTestIfCopyInsert(tcTestExcelDTO.getIfCopyInsert());
            updateCerSampleTestTb.setTestFixedPointType(tcTestExcelDTO.getFixedPointType());
            updateCerSampleTestTb.setTestDonorResidueInfo(tcTestExcelDTO.getDonorResidueInfo());
            updateCerSampleTestTb.setTestInsertionSite(tcTestExcelDTO.getInsertionSite());
            updateCerSampleTestTb.setTestElisaResult(tcTestExcelDTO.getElisaResult());
            updateCerSampleTestTb.setTestQbzrSeq(tcTestExcelDTO.getQbzrSeq());
            updateCerSampleTestTb.setTestEditResidueInfo(tcTestExcelDTO.getEditResidueInfo());
            updateCerSampleTestTb.setTestUserId(SecurityContextHolder.getUserId());
            updateCerSampleTestTb.setTestUserName(SecurityContextHolder.getUserName());
            updateCerSampleTestTb.setTestTime(DateUtil.formatDate(new Date()));
            updateList.add(updateCerSampleTestTb);
        }

        tcSampleTestTaskDTO.setTestDataExcelUrl(tcSampleTestUploadTestTemplateReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcSampleTestTaskDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        tcSampleTestTbMapper.updateBatchById(updateList);
    }

    @Override
    public void downIdentifyPrimerTemplate(HttpServletResponse response, String applyNo) {
        try {
            List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(applyNo);
            List<IdentifyPrimerTemplateExcelDTO> identifyPrimerTemplateExcelDTOList = new ArrayList<IdentifyPrimerTemplateExcelDTO>();
            for (TcSampleTestTb tcSampleTestTb : tcSampleTestTbList) {
                IdentifyPrimerTemplateExcelDTO identifyPrimerTemplateExcelDTO = new IdentifyPrimerTemplateExcelDTO();
                identifyPrimerTemplateExcelDTO.setExperimentNum(tcSampleTestTb.getExperimentNum());
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
    public void uploadIdentifyPrimerTemplate(TcSampleTestUploadIdentifyPrimerTemplateReqDTO tcSampleTestUploadIdentifyPrimerTemplateReqDTO) {

        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcSampleTestUploadIdentifyPrimerTemplateReqDTO.getApplyNo());

        TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);

        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcSampleTestUploadIdentifyPrimerTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, tcSampleTestUploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【田测鉴定引物文件】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<IdentifyPrimerTemplateExcelDTO> identifyPrimerTemplateExcelDTOList = ExcelUtil.readExcel(tempFilePath, IdentifyPrimerTemplateExcelDTO.class);

        if (CollectionUtil.isNotEmpty(identifyPrimerTemplateExcelDTOList)) {
            for (IdentifyPrimerTemplateExcelDTO identifyPrimerTemplateExcelDTO : identifyPrimerTemplateExcelDTOList) {
                TcSampleTestTb tcSampleTestTb = tcSampleTestTbMapper.selectOneByExperimentNumAndSampleCode(tcSampleTestTaskDTO.getExperimentNum(), identifyPrimerTemplateExcelDTO.getSampleCode());
                if (tcSampleTestTb != null) {
                    tcSampleTestTb.setIdentifyPrimer(identifyPrimerTemplateExcelDTO.getIdentifyPrimer());
                    tcSampleTestTbMapper.updateIdentifyPrimerById(identifyPrimerTemplateExcelDTO.getIdentifyPrimer(), tcSampleTestTb.getId());
                }
            }
        }

        tcSampleTestTaskDTO.setIdentifyPrimerTemplateExcelUrl(tcSampleTestUploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcSampleTestTaskDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

        //默认排版
        TcSampleTestLayoutConfirmReqDTO tcSampleTestLayoutConfirmReqDTO = getLayoutConfirmReqDTO(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(tcSampleTestLayoutConfirmReqDTO.getNinetySixList()) || CollectionUtil.isNotEmpty(tcSampleTestLayoutConfirmReqDTO.getSingleList())) {
            //入库
            layoutConfirm(tcSampleTestLayoutConfirmReqDTO);
        }
    }

    @Override
    public TcSampleTestLayoutPreviewRspDTO layoutPreview(String applyNo) {
        TcSampleLayoutTb tcSampleLayoutTb = tcSampleLayoutTbMapper.selectOneByApplyNo(applyNo);
        if (tcSampleLayoutTb == null) {
            return getLayoutPreviewRspDTO(applyNo);
        } else {
            TcSampleTestLayoutPreviewRspDTO tcSampleTestLayoutPreviewRspDTO = new TcSampleTestLayoutPreviewRspDTO();
            tcSampleTestLayoutPreviewRspDTO.setSingleList(JSONUtil.toList(tcSampleLayoutTb.getSingleContent(), SampleUnitDTO.class));
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
            tcSampleTestLayoutPreviewRspDTO.setNinetySixList(ninetySixList);
            return tcSampleTestLayoutPreviewRspDTO;
        }
    }

    @Override
    public void layoutConfirm(TcSampleTestLayoutConfirmReqDTO tcSampleTestLayoutConfirmReqDTO) {
        TcSampleLayoutTb tcSampleLayoutTb = tcSampleLayoutTbMapper.selectOneByApplyNo(tcSampleTestLayoutConfirmReqDTO.getApplyNo());
        if (tcSampleLayoutTb != null) {
            tcSampleLayoutTb.setSingleContent(JSONUtil.toJsonStr(tcSampleTestLayoutConfirmReqDTO.getSingleList()));
            tcSampleLayoutTb.setPlateContent(JSONUtil.toJsonStr(tcSampleTestLayoutConfirmReqDTO.getNinetySixList()));
            tcSampleLayoutTb.setCreateTime(new Date());
            tcSampleLayoutTbMapper.updateById(tcSampleLayoutTb);
        } else {
            tcSampleLayoutTb = new TcSampleLayoutTb();
            tcSampleLayoutTb.setSingleContent(JSONUtil.toJsonStr(tcSampleTestLayoutConfirmReqDTO.getSingleList()));
            tcSampleLayoutTb.setPlateContent(JSONUtil.toJsonStr(tcSampleTestLayoutConfirmReqDTO.getNinetySixList()));
            tcSampleLayoutTb.setCreateTime(new Date());
            tcSampleLayoutTb.setApplyNo(tcSampleTestLayoutConfirmReqDTO.getApplyNo());
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
            TcSampleTestLayoutConfirmReqDTO tcSampleTestLayoutConfirmReqDTO = getLayoutConfirmReqDTO(applyNo);
            if (CollectionUtil.isNotEmpty(tcSampleTestLayoutConfirmReqDTO.getNinetySixList()) || CollectionUtil.isNotEmpty(tcSampleTestLayoutConfirmReqDTO.getSingleList())) {
                //入库
                layoutConfirm(tcSampleTestLayoutConfirmReqDTO);
                //再次下载
                dowLayoutExcel(applyNo, httpServletResponse);
            }

        }
    }

    @Override
    public void approveSampleResult(TcSampleTestApproveSampleResultReqDTO tcSampleTestApproveSampleResultReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcSampleTestApproveSampleResultReqDTO.getTaskNum());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("不是执行中工单");
        }
        TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
        for (TcSampleTestApproveSampleResultReqDTO.Content content : tcSampleTestApproveSampleResultReqDTO.getContentList()) {
            TcSampleTestTb tcSampleTestTb = tcSampleTestTbMapper.selectOneByExperimentNumAndSampleCode(tcSampleTestTaskDTO.getExperimentNum(), content.getSampleCode());
            if (tcSampleTestTb == null) {
                log.error("approveSampleResult content={}", content);
                throw new BusinessException("此试验中无此取样编号:" + content.getSampleCode() + ", 实现号：" + tcSampleTestTaskDTO.getExperimentNum());
            }
            tcSampleTestTb.setCheckResult(content.getCheckResult());
            tcSampleTestTbMapper.updateById(tcSampleTestTb);
        }
    }

    @Override
    public List<TcSampleTestQueryListBySampleCodeListRspDTO> queryListBySampleCodeList(TcSampleTestQueryListBySampleCodeListReqDTO tcSampleTestQueryListBySampleCodeListReqDTO) {
        List<TcSampleTestQueryListBySampleCodeListRspDTO> result = new ArrayList<>();
        List<String> sampleCodeList = tcSampleTestQueryListBySampleCodeListReqDTO.getContentList().stream().map(TcSampleTestQueryListBySampleCodeListReqDTO.Content::getSampleCode).collect(Collectors.toList());
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleCodeInAndApplyType(sampleCodeList, SampleTestApplyTypeEnum.first.name());
        if (CollectionUtil.isNotEmpty(tcSampleTestTbList)) {
            return BeanUtils.copyListProperties(tcSampleTestTbList, TcSampleTestQueryListBySampleCodeListRspDTO.class);
        }
        return result;
    }

    @Override
    public void uploadBioInfoSampleTestResult(TcSampleTestUploadBioInfoSampleTestResultReqDTO tcSampleTestUploadBioInfoSampleTestResultReqDTO) {

    }

    @Override
    public List<TcSampleTestQueryBioInfoSampleTestResultRspDTO> queryBioInfoSampleTestResult(Integer id) {
        return null;
    }

    @Override
    public void bioInfoSampleTestResultConfirm(TcSampleTestBioInfoSampleTestResultConfirmReqDTO tcSampleTestBioInfoSampleTestResultConfirmReqDTO) {

    }

    @Override
    public void synBioInfoSampleTestResult(Integer id) {

    }

    @Override
    public Object bioInfoSampleTestResultDetail(Integer bioInfoId) {
        return null;
    }

    @Override
    public Integer bioInfoHead(String applyNo) {
        return null;
    }

    @Override
    public PageInfo<TcSampleTestBioInfoPageRspDTO> bioInfoPage(TcSampleTestBioInfoPageReqDTO tcSampleTestBioInfoPageReqDTO) {
        return null;
    }


    private TcSampleTestLayoutConfirmReqDTO getLayoutConfirmReqDTO(String applyNo) {
        TcSampleTestLayoutConfirmReqDTO tcSampleTestLayoutConfirmReqDTO = new TcSampleTestLayoutConfirmReqDTO();
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(applyNo);
        if (CollectionUtil.isEmpty(tcSampleTestTbList)) {
            return tcSampleTestLayoutConfirmReqDTO;
        }
        List<TcSampleTestTb> noIdentifyPrimerList = tcSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noIdentifyPrimerList)) {
            noIdentifyPrimerList.forEach(tcSampleTestTb -> {
                tcSampleTestLayoutConfirmReqDTO.fillSampleToSingleList(tcSampleTestTb.getVectorTaskCode(), tcSampleTestTb.getExperimentNum(), tcSampleTestTb.getRegionNum(), tcSampleTestTb.getSeedNum(), tcSampleTestTb.getSampleCode(), tcSampleTestTb.getIdentifyPrimer());
            });
        }
        //96孔板
        List<TcSampleTestTb> identifyPrimerList = tcSampleTestTbList.stream().filter(tcSampleTestTb -> StringUtils.isNotEmpty(tcSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(identifyPrimerList)) {
            tcSampleTestLayoutConfirmReqDTO.setNinetySixList(LayoutUtil.fillSampleToNinetySixList(identifyPrimerList));
        }
        tcSampleTestLayoutConfirmReqDTO.setApplyNo(applyNo);
        return tcSampleTestLayoutConfirmReqDTO;
    }


    private TcSampleTestLayoutPreviewRspDTO getLayoutPreviewRspDTO(String applyNo) {
        TcSampleTestLayoutPreviewRspDTO tcSampleTestLayoutPreviewRspDTO = new TcSampleTestLayoutPreviewRspDTO();
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(applyNo);
        if (CollectionUtil.isEmpty(tcSampleTestTbList)) {
            return tcSampleTestLayoutPreviewRspDTO;
        }
        List<TcSampleTestTb> noIdentifyPrimerList = tcSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noIdentifyPrimerList)) {
            noIdentifyPrimerList.forEach(tcSampleTestTb -> {
                tcSampleTestLayoutPreviewRspDTO.fillSampleToSingleList(tcSampleTestTb.getVectorTaskCode(), tcSampleTestTb.getExperimentNum(), tcSampleTestTb.getRegionNum(), tcSampleTestTb.getSeedNum(), tcSampleTestTb.getSampleCode(), tcSampleTestTb.getIdentifyPrimer());
            });
        }
        //96孔板
        List<TcSampleTestTb> identifyPrimerList = tcSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isNotEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(identifyPrimerList)) {
            tcSampleTestLayoutPreviewRspDTO.setNinetySixList(LayoutUtil.fillSampleToNinetySixList(identifyPrimerList));
        }

        return tcSampleTestLayoutPreviewRspDTO;
    }
}
