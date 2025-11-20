package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.CheckResultEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.TestChannelEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.base.SampleUnitDTO;
import com.bio.drqi.manage.dto.project.*;
import com.bio.drqi.manage.sample.req.*;
import com.bio.drqi.manage.sample.rsp.*;
import com.bio.drqi.manage.service.common.SynSampleTestResultService;
import com.bio.drqi.manage.service.project.SampleTestService;
import com.bio.drqi.manage.util.LayoutUtil;
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

    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;

    @Resource
    private SynSampleTestResultService synSampleTestResultService;


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
    private BioSampleTestTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;


    @Resource
    private OssService ossService;


    @Override
    public PageInfo<SampleTestListDetailRspDTO> listPage(SampleTestListDetailReqDTO sampleTestListDetailReqDTO) {
        PageHelper.startPage(sampleTestListDetailReqDTO.getPageNum(), sampleTestListDetailReqDTO.getPageSize());
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectSelective(BeanUtils.copyProperties(sampleTestListDetailReqDTO, CerSampleTestTb.class));
        PageInfo<CerSampleTestTb> srcPageInfo = new PageInfo<>(cerSampleTestTbList);
        if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
            return new PageInfo<SampleTestListDetailRspDTO>();
        }
        PageInfo<SampleTestListDetailRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, SampleTestListDetailRspDTO.class);
        targetPageInfo.getList().forEach(sampleTestListDetailRspDTO -> {
            sampleTestListDetailRspDTO.setSampleGeneration(GenerationEnum.getGenerationDesc(sampleTestListDetailRspDTO.getSampleGeneration()));
            List<BioSampleTestTwoResultTb> bioSampleTestTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByApplyNoAndSampleCodeOrderByIdDesc(sampleTestListDetailRspDTO.getApplyNo(), sampleTestListDetailRspDTO.getSampleCode());
            if(CollectionUtil.isNotEmpty(bioSampleTestTwoResultTbList)){
                List<BioSampleTestTwoResultDetailTb> cerSampleTestBioInfoResultDetailList = bioSampleSampleTwoResultDetailTbMapper.selectAllByTwoResultIdAndConfirmStatus(bioSampleTestTwoResultTbList.get(0).getId(), "checked");
                if (CollectionUtil.isNotEmpty(cerSampleTestBioInfoResultDetailList)) {
                    cerSampleTestBioInfoResultDetailList.forEach(cerSampleTestBioInfoResultTb -> {
                        sampleTestListDetailRspDTO.addBioInfoResultToList(cerSampleTestBioInfoResultTb.getSampleId(), cerSampleTestBioInfoResultTb.getVarType(), cerSampleTestBioInfoResultTb.getMutate(), cerSampleTestBioInfoResultTb.getRatio());
                    });
                }
            }

            sampleTestListDetailRspDTO.setMatchNum(CollectionUtil.isNotEmpty(sampleTestListDetailRspDTO.getBioInfoResultList()) ? sampleTestListDetailRspDTO.getBioInfoResultList().size() : 0);
        });
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
    @Transactional(rollbackFor = Exception.class)
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
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        newSampleTestDTO.setSampleDataExcelUrl(uploadSampleTemplateReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        cerSampleTestTbMapper.updateBatchById(updateList);

    }

    @Override
    public void downTestTemplate(DownTestTemplateReqDTO downTestTemplateReqDTO, HttpServletResponse response) {
        List<TestExcelDTO> testExcelDTOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(downTestTemplateReqDTO.getApplyNo())) {
            List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(downTestTemplateReqDTO.getApplyNo());
            for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
                TestExcelDTO testExcelDTO = new TestExcelDTO();
                testExcelDTO.setSampleCode(cerSampleTestTb.getSampleCode());
                testExcelDTO.setSampleTime(cerSampleTestTb.getSampleTime());
                testExcelDTO.setGeneration(cerSampleTestTb.getSampleGeneration());
                testExcelDTO.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
                testExcelDTOList.add(testExcelDTO);
            }
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
    @Transactional(rollbackFor = Exception.class)
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

        CerSampleApplyTb cerSampleApplyTb = cerSampleApplyTbMapper.selectOneByApplyNo(uploadTestTemplateReqDTO.getApplyNo());
        if (cerSampleApplyTb == null) {
            throw new BusinessException("数据异常，找不到申请数据");
        }

        List<CerSampleTestTb> updateList = new ArrayList<>();
        List<BioSampleTestOneResultTb> bioSampleSampleOneResultTbList = new ArrayList<>();
        Map<String, CerSampleTestTb> cerSampleTestTbMap = cerSampleTestTbList.stream().collect(Collectors.toMap(CerSampleTestTb::getSampleCode, cerSampleTestTb -> cerSampleTestTb));
        for (TestExcelDTO testExcelDTO : testExcelDTOList) {
            log.info("检测数据上送 数据处理中：" + testExcelDTO.getSampleCode());
            CerSampleTestTb cerSampleTestTb = cerSampleTestTbMap.get(testExcelDTO.getSampleCode());
            CerSampleTestTb updateCerSampleTestTb = new CerSampleTestTb();
            updateCerSampleTestTb.setId(cerSampleTestTb.getId());
            updateCerSampleTestTb.setSampleCode(cerSampleTestTb.getSampleCode());
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
            if (updateCerSampleTestTb.ifHaveTestResult()) {
                updateCerSampleTestTb.setTestUserId(SecurityContextHolder.getUserId());
                updateCerSampleTestTb.setTestUserName(SecurityContextHolder.getNickName());
                updateCerSampleTestTb.setTestTime(DateUtil.formatDate(new Date()));
            }
            updateCerSampleTestTb.setUpdateTime(new Date());
            updateList.add(updateCerSampleTestTb);
            bioSampleSampleOneResultTbList.add(BioSampleTestOneResultTb.of(updateCerSampleTestTb, TestChannelEnum.project.name(), uploadTestTemplateReqDTO.getApplyNo(), null));

        }


        cerSampleApplyTb.setOneTestExcelUrl(uploadTestTemplateReqDTO.getExcelUrl());
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        newSampleTestDTO.setTestDataExcelUrl(uploadTestTemplateReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
        //清空旧数据
        bioSampleTestOneResultTbMapper.deleteByTaskNum(uploadTestTemplateReqDTO.getApplyNo());

        //插入新数据
        bioSampleTestOneResultTbMapper.insertBatch(bioSampleSampleOneResultTbList);
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        cerSampleTestTbMapper.updateBatchById(updateList);
        cerSampleApplyTbMapper.updateById(cerSampleApplyTb);
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
                IdentifyPrimerTemplateExcelDTO identifyPrimerTemplateExcelDTO = new IdentifyPrimerTemplateExcelDTO();
                identifyPrimerTemplateExcelDTO.setTransformCode(cerSampleTestTb.getTransformCode());
                identifyPrimerTemplateExcelDTO.setSampleCode(cerSampleTestTb.getSampleCode());
                identifyPrimerTemplateExcelDTO.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
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
    @Transactional(rollbackFor = Exception.class)
    public void uploadIdentifyPrimerTemplate(UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {

        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(uploadIdentifyPrimerTemplateReqDTO.getApplyNo());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行中任务");
        }
        if (!uploadIdentifyPrimerTemplateReqDTO.getExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        CerSampleApplyTb cerSampleApplyTb = cerSampleApplyTbMapper.selectOneByApplyNo(uploadIdentifyPrimerTemplateReqDTO.getApplyNo());
        if (cerSampleApplyTb == null) {
            throw new BusinessException("数据异常，找不到申请数据");
        }
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
        cerSampleApplyTb.setIdentifyExcelUrl(uploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        newSampleTestDTO.setIdentifyPrimerTemplateExcelUrl(uploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        cerSampleApplyTbMapper.updateById(cerSampleApplyTb);

        //默认排版
        LayoutConfirmReqDTO layoutConfirmReqDTO = getLayoutConfirmReqDTO(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(layoutConfirmReqDTO.getNinetySixList()) || CollectionUtil.isNotEmpty(layoutConfirmReqDTO.getSingleList())) {
            //入库
            layoutConfirm(layoutConfirmReqDTO);
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
        List<CerSampleTestTb> identifyPrimerList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isNotEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(identifyPrimerList)) {
            layoutPreviewRspDTO.setNinetySixList(LayoutUtil.fillSampleToNinetySixList(identifyPrimerList));
        }

        return layoutPreviewRspDTO;
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
        List<CerSampleTestTb> identifyPrimerList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isNotEmpty(cerSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(identifyPrimerList)) {
            layoutConfirmReqDTO.setNinetySixList(LayoutUtil.fillSampleToNinetySixList(identifyPrimerList));
        }
        layoutConfirmReqDTO.setApplyNo(applyNo);
        return layoutConfirmReqDTO;
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
    public CountNumByApplyNoRspDTO countNumByApplyNo(String applyNo) {
        CountNumByApplyNoRspDTO countNumByApplyNoRspDTO = new CountNumByApplyNoRspDTO();
        Integer totalCount = cerSampleTestTbMapper.selectCountByApplyNo(applyNo);
        Integer checkNum = cerSampleTestTbMapper.selectCountByApplyNoAndCheckResultIsNotNull(applyNo);
        countNumByApplyNoRspDTO.setCheckNum(checkNum);
        countNumByApplyNoRspDTO.setTotalNum(totalCount);
        return countNumByApplyNoRspDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadBioInfoSampleTestResult(UploadBioInfoSampleTestResultReqDTO uploadBioInfoSampleTestResultReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行中任务，无法进行操作");
        }
        if (!uploadBioInfoSampleTestResultReqDTO.getExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        CerSampleApplyTb cerSampleApplyTb = cerSampleApplyTbMapper.selectOneByApplyNo(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
        if (cerSampleApplyTb == null) {
            throw new BusinessException("数据异常，找不到申请数据");
        }
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        newSampleTestDTO.setBioInfoResultExcelUrl(uploadBioInfoSampleTestResultReqDTO.getExcelUrl());

        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + uploadBioInfoSampleTestResultReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, uploadBioInfoSampleTestResultReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【生信检测结果】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        Date currentDate = new Date();
        //解析excel
        List<SampleTestBioInfoExcelDTO> sampleTestBioInfoExcelDTOList = ExcelUtil.readExcel(tempFilePath, SampleTestBioInfoExcelDTO.class);
        if (CollectionUtil.isEmpty(sampleTestBioInfoExcelDTOList)) {
            throw new BusinessException("excel无数据");
        }
        sampleTestBioInfoExcelDTOList = sampleTestBioInfoExcelDTOList.stream().filter(sampleTestBioInfoExcelDTO -> StringUtils.isNotEmpty(sampleTestBioInfoExcelDTO.getSampleId()) && StringUtils.isNotEmpty(sampleTestBioInfoExcelDTO.getRunId())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(sampleTestBioInfoExcelDTOList)) {
            throw new BusinessException("excel数据异常或者格式不对");
        }
        if (sampleTestBioInfoExcelDTOList.stream().map(SampleTestBioInfoExcelDTO::getSampleCode).collect(Collectors.toList()).size() != sampleTestBioInfoExcelDTOList.size()) {
            throw new BusinessException("excel中有重复取样编号");
        }

        //删除旧数据
        bioSampleTestTwoResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
        Map<String, CerSampleTestTb> stringCerSampleTestTbMap = cerSampleTestTbList.stream().collect(Collectors.toMap(CerSampleTestTb::getSampleCode, cerSampleTestTb -> cerSampleTestTb));
        List<BioSampleTestTwoResultTb> bioSampleSampleTwoResultTbList = new ArrayList<>();
        List<CerSampleTestTb> updateCerSampleTestTbList = new ArrayList<>();

        //组装BioSampleSampleTwoResultTb数据
        for (SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO : sampleTestBioInfoExcelDTOList) {
            CerSampleTestTb cerSampleTestTb = stringCerSampleTestTbMap.get(sampleTestBioInfoExcelDTO.getSampleCode());
            if (cerSampleTestTb == null) {
                continue;
            }
            BioSampleTestTwoResultTb bioSampleSampleTwoResultTb = new BioSampleTestTwoResultTb();
            bioSampleSampleTwoResultTb.setApplyNo(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
            bioSampleSampleTwoResultTb.setUploadNum(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
            bioSampleSampleTwoResultTb.setSampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
            bioSampleSampleTwoResultTb.setSampleId(sampleTestBioInfoExcelDTO.getSampleId());
            bioSampleSampleTwoResultTb.setRunId(sampleTestBioInfoExcelDTO.getRunId());
            bioSampleSampleTwoResultTb.setCreateTime(currentDate);
            bioSampleSampleTwoResultTb.setTestChannel(TestChannelEnum.project.name());
            bioSampleSampleTwoResultTbList.add(bioSampleSampleTwoResultTb);
            //更新检测人（检测标志）
            updateCerSampleTestTbList.add(CerSampleTestTb.builder().id(cerSampleTestTb.getId()).testUserId(SecurityContextHolder.getUserId()).testUserName(SecurityContextHolder.getNickName()).build());
        }

        //更新检测结果
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultTbList)) {
            //异步同步结果
            List<BioSampleTestTwoResultDetailTb> bioSampleTwoResultDetailTbList = synSampleTestResultService.synBioResult(bioSampleSampleTwoResultTbList);

            bioSampleTestTwoResultTbMapper.insertBatch(bioSampleSampleTwoResultTbList);
            cerSampleTestTbMapper.updateBatchById(updateCerSampleTestTbList);
            if (CollectionUtil.isNotEmpty(bioSampleTwoResultDetailTbList)) {

                bioSampleTwoResultDetailTbList.forEach(bioSampleSampleTwoResultDetailTb -> {
                    bioSampleSampleTwoResultDetailTbMapper.deleteByTwoResultId(bioSampleSampleTwoResultDetailTb.getTwoResultId());
                });
                bioSampleSampleTwoResultDetailTbMapper.insertBatch(bioSampleTwoResultDetailTbList);
            }

        }

        cerSampleApplyTb.setNgsExcelUrl(uploadBioInfoSampleTestResultReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        cerSampleApplyTbMapper.updateById(cerSampleApplyTb);
    }

    @Override
    public List<QueryBioInfoSampleTestResultRspDTO> queryBioInfoSampleTestResult(Integer id) {
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectById(id);
        if (cerSampleTestTb == null) {
            throw new BusinessException("参数错误，找不到此取样信息：" + id);
        }
        List<BioSampleTestTwoResultTb> bioSampleTestTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByApplyNoAndSampleCodeOrderByIdDesc(cerSampleTestTb.getApplyNo(), cerSampleTestTb.getSampleCode());
        if (CollectionUtil.isEmpty(bioSampleTestTwoResultTbList)) {
            throw new BusinessException("没有上传NGS检测结果");
        }
        List<BioSampleTestTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = bioSampleSampleTwoResultDetailTbMapper.selectAllByTwoResultIdAndConfirmStatus(bioSampleTestTwoResultTbList.get(0).getId(), "checked");
        return BeanUtils.copyListProperties(bioSampleSampleTwoResultDetailTbList, QueryBioInfoSampleTestResultRspDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bioInfoSampleTestResultConfirm(BioInfoSampleTestResultConfirmReqDTO bioInfoSampleTestResultConfirmReqDTO) {
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectById(bioInfoSampleTestResultConfirmReqDTO.getId());

        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerSampleTestTb.getApplyNo());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行中任务，无法进行操作");
        }
        List<BioSampleTestTwoResultDetailTb> cerSampleTestBioInfoResultTbList = bioSampleSampleTwoResultDetailTbMapper.selectAllByApplyNoAndSampleCode(cerSampleTestTb.getApplyNo(), cerSampleTestTb.getSampleCode());
        cerSampleTestBioInfoResultTbList.forEach(cerSampleTestBioInfoResultTb -> {
            if (!bioInfoSampleTestResultConfirmReqDTO.getBioInfoIdList().contains(cerSampleTestBioInfoResultTb.getId())) {
                bioSampleSampleTwoResultDetailTbMapper.deleteById(cerSampleTestBioInfoResultTb.getId());
            }
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synBioInfoSampleTestResult(Integer id) {
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectById(id);
        List<BioSampleTestTwoResultTb> bioSampleSampleTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByApplyNoAndSampleCode(cerSampleTestTb.getApplyNo(), cerSampleTestTb.getSampleCode());
        if (CollectionUtil.isEmpty(bioSampleSampleTwoResultTbList)) {
            throw new BusinessException("excel没匹配到该生信检测数据");
        }
        List<BioSampleTestTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = synSampleTestResultService.synBioResult(Arrays.asList(bioSampleSampleTwoResultTbList.get(0)));
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultDetailTbList)) {
            bioSampleSampleTwoResultDetailTbMapper.deleteByApplyNoAndSampleCode(cerSampleTestTb.getApplyNo(), cerSampleTestTb.getSampleCode());
            for (BioSampleTestTwoResultDetailTb cerSampleTestBioInfoResultTb : bioSampleSampleTwoResultDetailTbList) {
                bioSampleSampleTwoResultDetailTbMapper.insert(cerSampleTestBioInfoResultTb);
            }
        }
        //更新结果状态
        bioSampleTestTwoResultTbMapper.updateById(bioSampleSampleTwoResultTbList.get(0));

    }

    @Override
    public void remark(SampleRemarkReqDTO sampleRemarkReqDTO) {
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectById(sampleRemarkReqDTO.getId());
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerSampleTestTb.getApplyNo());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("执行中工单可以进行该操作");
        }
        cerSampleTestTb.setRemark(sampleRemarkReqDTO.getRemark());
        cerSampleTestTbMapper.updateById(cerSampleTestTb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadTargetResultTemplate(SampleTestUploadTargetResultTemplateReqDTO sampleTestUploadTargetResultTemplateReqDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + sampleTestUploadTargetResultTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, sampleTestUploadTargetResultTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【取样结果标记】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<List<Object>> list = ExcelUtil.readExcel(tempFilePath);
        List<String> sampleCodeList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (int i = 1; i < list.size(); i++) {
                CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByApplyNoAndSampleCode(sampleTestUploadTargetResultTemplateReqDTO.getTaskNum(), list.get(i).get(0).toString());
                if (cerSampleTestTb == null) {
                    throw new BusinessException("无此取样编号:" + list.get(i).get(0).toString());
                }
                sampleCodeList.add(cerSampleTestTb.getSampleCode());
            }
        }
        cerSampleTestTbMapper.updateTargetFlagByApplyNo(null, sampleTestUploadTargetResultTemplateReqDTO.getTaskNum());
        cerSampleTestTbMapper.updateTargetFlagByApplyNoAndSampleCodeIn(BioDrQiContents.Y, sampleTestUploadTargetResultTemplateReqDTO.getTaskNum(), sampleCodeList);
        cerSampleTestTbMapper.updateCheckResultByApplyNoAndSampleCodeIn(CheckResultEnum.stay.name(), sampleTestUploadTargetResultTemplateReqDTO.getTaskNum(), sampleCodeList);
        cerSampleTestTbMapper.updateCheckResultByApplyNoAndSampleCodeNotIn(CheckResultEnum.remove.name(), sampleTestUploadTargetResultTemplateReqDTO.getTaskNum(), sampleCodeList);
    }

    @Override
    public List<CountCheckResultRspDTO> countCheckResult(String applyNo) {
        List<CountCheckResultRspDTO> list = new ArrayList<>();
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectCountNumByApplyNo(applyNo);
        if (CollectionUtil.isNotEmpty(cerSampleTestTbList)) {
            cerSampleTestTbList.forEach(cerSampleTestTb -> {
                CountCheckResultRspDTO countCheckResultRspDTO = new CountCheckResultRspDTO();
                countCheckResultRspDTO.setCheckResult(cerSampleTestTb.getCheckResult());
                countCheckResultRspDTO.setCountNum(cerSampleTestTb.getCountNum() == null ? 0 : cerSampleTestTb.getCountNum());
                list.add(countCheckResultRspDTO);
            });
            return list;
        }

        return new ArrayList<>();
    }

    @Override
    public CountTestResultRspDTO countTestResult(String applyNo) {
        CountTestResultRspDTO countTestResultRspDTO = new CountTestResultRspDTO();
        countTestResultRspDTO.setCheckResultNum(cerSampleTestTbMapper.selectTestResultCount(applyNo));
        countTestResultRspDTO.setTwoResultNum(cerSampleTestTbMapper.selectTowTestResultCount(applyNo));
        countTestResultRspDTO.setNotResultNum(cerSampleTestTbMapper.selectNoTestResultCount(applyNo));
        return countTestResultRspDTO;
    }


}
