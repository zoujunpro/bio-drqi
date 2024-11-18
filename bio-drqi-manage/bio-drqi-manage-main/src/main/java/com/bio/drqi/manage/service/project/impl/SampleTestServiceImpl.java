package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.bio.cer.base.SampleUnitDTO;
import com.bio.cer.enums.BioTaskStatusEnum;
import com.bio.cer.sample.req.*;
import com.bio.cer.sample.rsp.*;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.IdentifyPrimerTemplateExcelDTO;
import com.bio.drqi.manage.dto.project.SampleExcelDTO;
import com.bio.drqi.manage.dto.project.TestExcelDTO;
import com.bio.drqi.manage.service.project.SampleTestService;
import com.bio.drqi.manage.util.SampleExcelUtil;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SampleTestServiceImpl implements SampleTestService {
    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerSampleApplyTbMapper cerSampleApplyTbMapper;


    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;


    @Resource
    private CerSampleLayoutTbMapper cerSampleLayoutTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Resource
    private OssService ossService;

    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;


    @Override
    public PageInfo<SampleApplyRspDTO> listPage(SampleApplyListPageReqDTO sampleApplyListPageReqDTO) {
        PageHelper.startPage(sampleApplyListPageReqDTO.getPageNum(), sampleApplyListPageReqDTO.getPageSize());
        CerSampleApplyTb cerSampleApplyTb = new CerSampleApplyTb();
        cerSampleApplyTb.setApplyNo(sampleApplyListPageReqDTO.getApplyNo());
        List<CerSampleApplyTb> cerSampleApplyTbList = cerSampleApplyTbMapper.selectSelective(cerSampleApplyTb);
        PageInfo<CerSampleApplyTb> srcPage = new PageInfo<>(cerSampleApplyTbList);
        PageInfo<SampleApplyRspDTO> targetPage = BeanUtils.copyPageInfoProperties(srcPage, SampleApplyRspDTO.class);
        return targetPage;
    }

    @Override
    public PageInfo<SampleTestListDetailRspDTO> listDetail(SampleTestListDetailReqDTO sampleTestListDetailReqDTO) {
        PageHelper.startPage(sampleTestListDetailReqDTO.getPageNum(), sampleTestListDetailReqDTO.getPageSize());
        CerSampleTestTb cerSampleTestTb = new CerSampleTestTb();
        cerSampleTestTb.setApplyNo(sampleTestListDetailReqDTO.getApplyNo());
        cerSampleTestTb.setVectorTaskId(sampleTestListDetailReqDTO.getVectorTaskId());
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectSelective(cerSampleTestTb);
        PageInfo<CerSampleTestTb> srcPageInfo = new PageInfo<>(cerSampleTestTbList);
        PageInfo<SampleTestListDetailRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, SampleTestListDetailRspDTO.class);
        return targetPageInfo;
    }

    @Override
    public List<SampleApplyRspDTO> listByVectorTask(SampleTestByVectorTaskReqDTO sampleTestByVectorTaskReqDTO) {
        List<CerSampleApplyTb> cerSampleApplyTb = cerSampleApplyTbMapper.selectAllByVectorTaskId(sampleTestByVectorTaskReqDTO.getVectorTaskId());
        return BeanUtils.copyListProperties(cerSampleApplyTb, SampleApplyRspDTO.class);
    }

    @Override
    public void downSampleTemplate(DownloadSampleTemplateReqDTO downloadSampleTemplateReqDTO, HttpServletResponse response) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(downloadSampleTemplateReqDTO.getApplyNo());
        if (bioTaskDtlTb == null) {
            throw new BusinessException("无此任务工单编号");
        }
        try {
            List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(downloadSampleTemplateReqDTO.getApplyNo());
            List<SampleExcelDTO> sampleExcelDTOList = new ArrayList<>();
            for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
                SampleExcelDTO sampleExcelDTO = new SampleExcelDTO();
                sampleExcelDTO.setTransformCode(cerSampleTestTb.getTransformCode());
                sampleExcelDTO.setSampleCode(cerSampleTestTb.getSampleCode());
                sampleExcelDTO.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
                sampleExcelDTOList.add(sampleExcelDTO);

            }
            String excelTemplateName = "取样数据上传模板_V1.xlsx";
            String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, sampleExcelDTOList, SampleExcelDTO.class, response);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }
    }

    @Override
    public void uploadSampleTemplate(UploadSampleTemplateReqDTO uploadSampleTemplateReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(uploadSampleTemplateReqDTO.getApplyNo());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行中任务");
        }
        if (!uploadSampleTemplateReqDTO.getExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + uploadSampleTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, uploadSampleTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<SampleExcelDTO> sampleExcelDTOList = ExcelUtil.readExcel(tempFilePath, SampleExcelDTO.class);

        if (sampleExcelDTOList == null || sampleExcelDTOList.size() == 0) {
            throw new BusinessException("无数据提交");
        }
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNoAndSampleCodeIn(uploadSampleTemplateReqDTO.getApplyNo(), sampleExcelDTOList.stream().map(SampleExcelDTO::getSampleCode).collect(Collectors.toList()));
        if (sampleExcelDTOList.size() != cerSampleTestTbList.size()) {
            throw new BusinessException("有取样编号不在此申请中");
        }
        List<CerSampleTestTb> updateList = new ArrayList<>();
        Map<String, CerSampleTestTb> cerSampleTestTbMap = cerSampleTestTbList.stream().collect(Collectors.toMap(CerSampleTestTb::getSampleCode, cerSampleTestTb -> cerSampleTestTb));
        for (SampleExcelDTO sampleExcelDTO : sampleExcelDTOList) {
            log.info("取样数据上送数据处理中：" + sampleExcelDTO.getSampleCode());
            ValidatorUtil.validator(sampleExcelDTO);
            CerSampleTestTb updateCerSampleTestTb = new CerSampleTestTb();
            updateCerSampleTestTb.setId(cerSampleTestTbMap.get(sampleExcelDTO.getSampleCode()).getId());
            updateCerSampleTestTb.setSampleTime(sampleExcelDTO.getSampleTime());
            updateCerSampleTestTb.setSampleRemark(sampleExcelDTO.getRemarks());
            updateCerSampleTestTb.setSampleGeneration(sampleExcelDTO.getGeneration());
            updateCerSampleTestTb.setSampleUserName(SecurityContextHolder.getNickName());
            updateCerSampleTestTb.setSampleUserId(SecurityContextHolder.getUserId());
            updateCerSampleTestTb.setUpdateTime(new Date());
            updateList.add(updateCerSampleTestTb);
        }
        cerSampleTestTbMapper.updateBatchById(updateList);

    }

    @Override
    public void downTestTemplate(DownTestTemplateReqDTO downTestTemplateReqDTO, HttpServletResponse response) {
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(downTestTemplateReqDTO.getApplyNo());
        List<TestExcelDTO> testExcelDTOList = new ArrayList<>();
        for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
            TestExcelDTO testExcelDTO = new TestExcelDTO();
            testExcelDTO.setSampleCode(cerSampleTestTb.getSampleCode());
            testExcelDTO.setSampleTime(cerSampleTestTb.getSampleTime());
            testExcelDTO.setGeneration(cerSampleTestTb.getSampleGeneration());
            testExcelDTO.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
            testExcelDTOList.add(testExcelDTO);
        }
        try {
            String excelTemplateName = "检测数据上传模板_V1.xlsx";
            String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, testExcelDTOList, TestExcelDTO.class, response);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }

    }

    @Override
    public void uploadTestTemplate(UploadTestTemplateReqDTO uploadTestTemplateReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(uploadTestTemplateReqDTO.getApplyNo());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行中任务");
        }
        if (!uploadTestTemplateReqDTO.getExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + uploadTestTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, uploadTestTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<TestExcelDTO> testExcelDTOList = ExcelUtil.readExcel(tempFilePath, TestExcelDTO.class);
        if (CollectionUtil.isEmpty(testExcelDTOList)) {
            throw new BusinessException("无数据提交");
        }
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNoAndSampleCodeIn(bioTaskDtlTb.getTaskNum(), testExcelDTOList.stream().map(TestExcelDTO::getSampleCode).collect(Collectors.toList()));
        if (testExcelDTOList.size() != cerSampleTestTbList.size()) {
            throw new BusinessException("有取样编号不存在此申请中");
        }
        List<CerSampleTestTb> updateList = new ArrayList<>();
        Map<String, CerSampleTestTb> cerSampleTestTbMap = cerSampleTestTbList.stream().collect(Collectors.toMap(CerSampleTestTb::getSampleCode, cerSampleTestTb -> cerSampleTestTb));
        for (TestExcelDTO testExcelDTO : testExcelDTOList) {
            log.info("检测数据上送 数据处理中：" + testExcelDTO.getSampleCode());
            CerSampleTestTb cerSampleTestTb = cerSampleTestTbMap.get(testExcelDTO.getSampleCode());
            CerSampleTestTb updateCerSampleTestTb = new CerSampleTestTb();
            updateCerSampleTestTb.setId(cerSampleTestTb.getId());
            updateCerSampleTestTb.setTestIdentifyPrimer(testExcelDTO.getIdentifyPrimer());
            updateCerSampleTestTb.setTestMethod(testExcelDTO.getTestMethod());
            updateCerSampleTestTb.setTestEditType(testExcelDTO.getEditType());
            updateCerSampleTestTb.setTestNoTransIdentityPrimer(testExcelDTO.getNoTransIdentityPrimer());
            updateCerSampleTestTb.setTestIsGeneModifyPositive(testExcelDTO.getIsGeneModifyPositive());
            updateCerSampleTestTb.setTestIfFixedPoint(testExcelDTO.getIfFixedPoint());
            updateCerSampleTestTb.setTestIfCopyInsert(testExcelDTO.getIfCopyInsert());
            updateCerSampleTestTb.setTestFixedPointType(testExcelDTO.getFixedPointType());
            updateCerSampleTestTb.setTestDonorResidueInfo(testExcelDTO.getDonorResidueInfo());
            updateCerSampleTestTb.setTestInsertionSite(testExcelDTO.getInsertionSite());
            updateCerSampleTestTb.setTestElisaResult(testExcelDTO.getElisaResult());
            updateCerSampleTestTb.setTestQbzrSeq(testExcelDTO.getQbzrSeq());
            updateCerSampleTestTb.setTestEditResidueInfo(testExcelDTO.getEditResidueInfo());
            updateCerSampleTestTb.setTestUserId(SecurityContextHolder.getUserId());
            updateCerSampleTestTb.setTestUserName(SecurityContextHolder.getUserName());
            updateCerSampleTestTb.setTestTime(DateUtil.formatDate(new Date()));
            updateCerSampleTestTb.setUpdateTime(new Date());
            updateList.add(updateCerSampleTestTb);
        }
        cerSampleTestTbMapper.updateBatchById(updateList);

    }

    @Override
    public List<SampleTestListDetailRspDTO> checkList(CheckListReqDTO checkListReqDTO) {
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(checkListReqDTO.getApplyNo());
        return BeanUtils.copyListProperties(cerSampleTestTbList, SampleTestListDetailRspDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveSampleResult(ApproveSampleResultReqDTO approveSampleResultReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(approveSampleResultReqDTO.getTaskNum());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("不是执行中工单");
        }
        for (ApproveSampleResultReqDTO.Content content : approveSampleResultReqDTO.getContentList()) {
            CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByApplyNoAndSampleCode(approveSampleResultReqDTO.getTaskNum(), content.getSampleCode());
            if (cerSampleTestTb == null) {
                log.error("approveSampleResult content={}", content);
                throw new BusinessException("此工单中无此取样编号:" + content.getSampleCode() + ", 工单：" + approveSampleResultReqDTO.getTaskNum());
            }
            cerSampleTestTb.setCheckResult(content.getCheckResult());
            cerSampleTestTb.setCheckUserName(SecurityContextHolder.getNickName());
            cerSampleTestTb.setCheckUserId(SecurityContextHolder.getUserId());
            cerSampleTestTb.setUpdateTime(new Date());
            cerSampleTestTbMapper.updateById(cerSampleTestTb);
        }
    }


    @Override
    public List<SampleCodeListRspDTO> findAllSampleCodeList(String vectorTaskCode) {
        List<SampleCodeListRspDTO> sampleCodeListRspDTOList = new ArrayList<>();
        //cerSampleTestTbMapper.selectAllByVectorTaskCodeAndTaskStatus(vectorTaskCode, SampleTaskStatusEnum.STATUS_12.status)
        List<CerSampleTestTb> cerSampleTestTbList = null;
        if (CollectionUtil.isNotEmpty(cerSampleTestTbList)) {
            cerSampleTestTbList.forEach(cerSampleTestTb -> {
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(cerSampleTestTb.getVectorTaskId());
                SampleCodeListRspDTO sampleCodeListRspDTO = new SampleCodeListRspDTO();
                sampleCodeListRspDTO.setSampleCode(cerSampleTestTb.getSampleCode());
                sampleCodeListRspDTO.setBreedName(cerVectorTaskTb.getAcceptorMaterial());
                sampleCodeListRspDTOList.add(sampleCodeListRspDTO);
            });
        }
        return sampleCodeListRspDTOList;
    }

    @Override
    public void downIdentifyPrimerTemplate(HttpServletResponse response, String applyNo) {
        try {
            List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(applyNo);
            List<IdentifyPrimerTemplateExcelDTO> identifyPrimerTemplateExcelDTOList = new ArrayList<IdentifyPrimerTemplateExcelDTO>();
            for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
                CerVectorGroupTb cerVectorGroupTb = cerVectorGroupTbMapper.selectOneByGroupNameAndVectorTaskId(cerSampleTestTb.getPlasmidName(), cerSampleTestTb.getVectorTaskId());
                IdentifyPrimerTemplateExcelDTO identifyPrimerTemplateExcelDTO = new IdentifyPrimerTemplateExcelDTO();
                identifyPrimerTemplateExcelDTO.setTransformCode(cerSampleTestTb.getTransformCode());
                identifyPrimerTemplateExcelDTO.setSampleCode(cerSampleTestTb.getSampleCode());
                identifyPrimerTemplateExcelDTO.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
                identifyPrimerTemplateExcelDTO.setPlasmidName(cerVectorGroupTb.getPlasmidNames());
                identifyPrimerTemplateExcelDTOList.add(identifyPrimerTemplateExcelDTO);

            }
            String excelTemplateName = "鉴定引物填写模板V1.0.xlsx";
            String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, identifyPrimerTemplateExcelDTOList, IdentifyPrimerTemplateExcelDTO.class, response);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("鉴定引物填写模板下载失败，请联系管理员检测模板配置");
        }
    }

    @Override
    public void uploadIdentifyPrimerTemplate(UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + uploadIdentifyPrimerTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, uploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【鉴定引物文件】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<IdentifyPrimerTemplateExcelDTO> identifyPrimerTemplateExcelDTOList = ExcelUtil.readExcel(tempFilePath, IdentifyPrimerTemplateExcelDTO.class);

        if (CollectionUtil.isNotEmpty(identifyPrimerTemplateExcelDTOList)) {
            for (IdentifyPrimerTemplateExcelDTO identifyPrimerTemplateExcelDTO : identifyPrimerTemplateExcelDTOList) {
                CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByApplyNoAndSampleCode(uploadIdentifyPrimerTemplateReqDTO.getApplyNo(), identifyPrimerTemplateExcelDTO.getSampleCode());
                if (cerSampleTestTb != null) {
                    cerSampleTestTb.setIdentifyPrimer(identifyPrimerTemplateExcelDTO.getIdentifyPrimer());
                    cerSampleTestTbMapper.updateIdentifyPrimerById(identifyPrimerTemplateExcelDTO.getIdentifyPrimer(), cerSampleTestTb.getId());
                }
            }
        }

    }

    /**
     * 逻辑：
     * 1.如果没有鉴定引物则进行单管取样
     * 2. 如果有鉴定引物，则不不同引物用不同孔板，如果某一孔板已经放满，则另起一版。一个孔板留4个位置
     *
     * @param applyNo
     * @return
     */
    @Override
    public LayoutPreviewRspDTO layoutPreview(String applyNo) {

        CerSampleLayoutTb cerSampleLayoutTb = cerSampleLayoutTbMapper.selectOneByApplyNo(applyNo);
        if (cerSampleLayoutTb == null) {
            return getLayoutPreviewRspDTO(applyNo);
        } else {
            LayoutPreviewRspDTO layoutPreviewRspDTO = new LayoutPreviewRspDTO();
            layoutPreviewRspDTO.setSingleList(JSONUtil.toList(cerSampleLayoutTb.getSingleContent(), SampleUnitDTO.class));
            JSONArray layoutListJsonArray = JSONUtil.parseArray(cerSampleLayoutTb.getPlateContent());
            List<List<SampleUnitDTO>> ninetySixList = new ArrayList<>();
            for (int i = 0; i < layoutListJsonArray.size(); i++) {
                List<SampleUnitDTO> layoutList = new ArrayList<>();
                JSONArray layoutJsonArray = JSONUtil.parseArray(layoutListJsonArray.get(i).toString());
                for (int j = 0; j < layoutJsonArray.size(); j++) {
                    List<SampleUnitDTO> rowList = JSONUtil.toList(layoutJsonArray.getJSONArray(j).toString(), SampleUnitDTO.class);
                    layoutList.addAll(rowList);
                }
                ninetySixList.add(layoutList);
            }
            layoutPreviewRspDTO.setNinetySixList(ninetySixList);
            return layoutPreviewRspDTO;
        }

    }

    private LayoutPreviewRspDTO getLayoutPreviewRspDTO(String applyNo) {
        LayoutPreviewRspDTO layoutPreviewRspDTO = new LayoutPreviewRspDTO();
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(applyNo);
        if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
            return layoutPreviewRspDTO;
        }
        List<CerSampleTestTb> noIdentifyPrimerList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noIdentifyPrimerList)) {
            noIdentifyPrimerList.forEach(cerSampleTestTb -> {
                layoutPreviewRspDTO.fillSampleToSingleList(cerSampleTestTb.getVectorTaskCode(), cerSampleTestTb.getTransformCode(), cerSampleTestTb.getSampleCode(), cerSampleTestTb.getIdentifyPrimer());
            });
        }
        //96孔板
        Map<String, List<CerSampleTestTb>> identifyPrimerListMap = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isNotEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.groupingBy(CerSampleTestTb::getIdentifyPrimer));
        if (CollectionUtil.isNotEmpty(identifyPrimerListMap)) {
            identifyPrimerListMap.forEach((identifyPrimer, identifyPrimerList) -> {
                layoutPreviewRspDTO.getNinetySixList().add(new ArrayList<SampleUnitDTO>());
                identifyPrimerList.stream().sorted(Comparator.comparing(CerSampleTestTb::getSampleCode)).forEach(cerSampleTestTb -> {
                    layoutPreviewRspDTO.fillSampleToNinetySixList(cerSampleTestTb.getVectorTaskCode(), cerSampleTestTb.getTransformCode(), cerSampleTestTb.getSampleCode(), cerSampleTestTb.getIdentifyPrimer());
                });
            });
        }
        return layoutPreviewRspDTO.restockSampleData();
    }

    private LayoutConfirmReqDTO getLayoutConfirmReqDTO(String applyNo) {
        LayoutConfirmReqDTO layoutConfirmReqDTO = new LayoutConfirmReqDTO();
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(applyNo);
        if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
            return layoutConfirmReqDTO;
        }
        List<CerSampleTestTb> noIdentifyPrimerList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noIdentifyPrimerList)) {
            noIdentifyPrimerList.forEach(cerSampleTestTb -> {
                layoutConfirmReqDTO.fillSampleToSingleList(cerSampleTestTb.getVectorTaskCode(), cerSampleTestTb.getTransformCode(), cerSampleTestTb.getSampleCode(), cerSampleTestTb.getIdentifyPrimer());
            });
        }
        //96孔板
        Map<String, List<CerSampleTestTb>> identifyPrimerListMap = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isNotEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.groupingBy(CerSampleTestTb::getIdentifyPrimer));
        if (CollectionUtil.isNotEmpty(identifyPrimerListMap)) {
            identifyPrimerListMap.forEach((identifyPrimer, identifyPrimerList) -> {
                layoutConfirmReqDTO.getNinetySixList().add(new ArrayList<List<SampleUnitDTO>>());
                identifyPrimerList.stream().sorted(Comparator.comparing(CerSampleTestTb::getSampleCode)).forEach(cerSampleTestTb -> {
                    layoutConfirmReqDTO.fillSampleToNinetySixList(cerSampleTestTb.getVectorTaskCode(), cerSampleTestTb.getTransformCode(), cerSampleTestTb.getSampleCode(), cerSampleTestTb.getIdentifyPrimer());
                });
            });
        }
        layoutConfirmReqDTO.setApplyNo(applyNo);
        return layoutConfirmReqDTO.restockSampleData();
    }

    @Override
    public void layoutConfirm(LayoutConfirmReqDTO layoutConfirmReqDTO) {
        CerSampleLayoutTb cerSampleLayoutTb = cerSampleLayoutTbMapper.selectOneByApplyNo(layoutConfirmReqDTO.getApplyNo());
        if (cerSampleLayoutTb != null) {
            cerSampleLayoutTb.setSingleContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getSingleList()));
            cerSampleLayoutTb.setPlateContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getNinetySixList()));
            cerSampleLayoutTb.setCreateTime(new Date());
            cerSampleLayoutTbMapper.updateById(cerSampleLayoutTb);
        } else {
            cerSampleLayoutTb = new CerSampleLayoutTb();
            cerSampleLayoutTb.setSingleContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getSingleList()));
            cerSampleLayoutTb.setPlateContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getNinetySixList()));
            cerSampleLayoutTb.setCreateTime(new Date());
            cerSampleLayoutTb.setApplyNo(layoutConfirmReqDTO.getApplyNo());
            cerSampleLayoutTbMapper.insert(cerSampleLayoutTb);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dowLayoutExcel(String applyNo, HttpServletResponse httpServletResponse) {
        CerSampleLayoutTb cerSampleLayoutTb = cerSampleLayoutTbMapper.selectOneByApplyNo(applyNo);
        List<List<List<SampleUnitDTO>>> layoutList = null;
        if (cerSampleLayoutTb != null) {
            List<SampleUnitDTO> singleSampleUnitDTOList = JSONUtil.toList(cerSampleLayoutTb.getSingleContent(), SampleUnitDTO.class);
            JSONArray layoutListJsonArray = JSONUtil.parseArray(cerSampleLayoutTb.getPlateContent());
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
            SampleExcelUtil.createExcel(applyNo, layoutList, singleSampleUnitDTOList, httpServletResponse, "取样标签排版.xlsx");
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

    @Override
    public List<SampleApplyRspDTO> sampleApplyListAll(String currentStepCode) {
        List<CerSampleApplyTb> cerSampleApplyTbList = cerSampleApplyTbMapper.selectAllByCurrentStepCode(currentStepCode);
        return BeanUtils.copyListProperties(cerSampleApplyTbList, SampleApplyRspDTO.class);
    }

    @Override
    public CountNumByApplyNoRspDTO countNumByApplyNo(String applyNo) {
        CountNumByApplyNoRspDTO countNumByApplyNoRspDTO = new CountNumByApplyNoRspDTO();
        Integer totalCount = cerSampleTestTbMapper.selectCountByApplyNo(applyNo);
        Integer checkNum = cerSampleTestTbMapper.selectCountByApplyNoAndCheckResultIsNotNull(applyNo);
        countNumByApplyNoRspDTO.setCheckNum(checkNum);
        countNumByApplyNoRspDTO.setTotalNum(totalCount);
        return countNumByApplyNoRspDTO;
    }


}
