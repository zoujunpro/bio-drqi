package com.bio.drqi.manage.service.bio.impl;

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
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.SampleTestApplyTypeEnum;
import com.bio.drqi.common.enums.TestChannelEnum;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.base.SampleUnitDTO;
import com.bio.drqi.manage.bio.req.BioSampleTestListDetailReqDTO;
import com.bio.drqi.manage.bio.req.BioSampleTestUploadTestTemplateReqDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleTestListDetailRspDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleTestQuerySpeciesByApplyTypeRspDTO;
import com.bio.drqi.manage.dto.bio.BioSampleTestResultExcelDTO;
import com.bio.drqi.manage.dto.bio.DownLoadIdentifyPrimerTemplateExcelDTO;
import com.bio.drqi.manage.dto.plant.SampleTestDownRepeatSampleTemplateExcelDTO;
import com.bio.drqi.manage.dto.project.NewSampleTestDTO;
import com.bio.drqi.manage.dto.project.SampleTestBioInfoExcelDTO;
import com.bio.drqi.manage.dto.project.TestExcelDTO;
import com.bio.drqi.manage.sample.req.*;
import com.bio.drqi.manage.sample.rsp.*;
import com.bio.drqi.manage.service.bio.BioSampleTestService;
import com.bio.drqi.manage.service.common.SynSampleTestResultService;
import com.bio.drqi.manage.util.SampleExcelUtil;
import com.bio.drqi.manage.util.SampleLayoutUtil;
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
public class BioSampleTestServiceImpl implements BioSampleTestService {

    @Resource
    private OssService ossService;


    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;


    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;

    @Resource
    private BioSampleTestResultFileTbMapper bioSampleTestResultFileTbMapper;

    @Resource
    private BioSampleLayoutTbMapper bioSampleLayoutTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;


    @Resource
    private SynSampleTestResultService synSampleTestResultService;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;


    @Override
    public PageInfo<BioSampleTestListDetailRspDTO> listPage(BioSampleTestListDetailReqDTO bioSampleTestListDetailReqDTO) {
        PageHelper.startPage(bioSampleTestListDetailReqDTO.getPageNum(), bioSampleTestListDetailReqDTO.getPageSize());
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectSelective(BeanUtils.copyProperties(bioSampleTestListDetailReqDTO, BioSampleTestTb.class));
        PageInfo<BioSampleTestTb> srcPageInfo = new PageInfo<>(bioSampleTestTbList);
        if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
            return new PageInfo<BioSampleTestListDetailRspDTO>();
        }
        PageInfo<BioSampleTestListDetailRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BioSampleTestListDetailRspDTO.class);
        targetPageInfo.getList().forEach(sampleTestListDetailRspDTO -> {
            sampleTestListDetailRspDTO.setGeneration(GenerationEnum.getGenerationDesc(sampleTestListDetailRspDTO.getGeneration()));
            List<BioSampleTestTwoResultTb> bioSampleTestTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByApplyNoAndSampleCodeOrderByIdDesc(sampleTestListDetailRspDTO.getApplyNo(), sampleTestListDetailRspDTO.getSampleCode());
            if (CollectionUtil.isNotEmpty(bioSampleTestTwoResultTbList)) {
                Map<String, List<BioSampleTestTwoResultTb>> bioSampleTestTwoResultTbListMap = bioSampleTestTwoResultTbList.stream().collect(Collectors.groupingBy(bioSampleTestTwoResultTb -> bioSampleTestTwoResultTb.getRunId() + bioSampleTestTwoResultTb.getSampleId()));
                bioSampleTestTwoResultTbListMap.forEach((key, bioSampleTestTwoResultTbs) -> {
                    List<BioSampleTestTwoResultDetailTb> cerSampleTestBioInfoResultDetailList = bioSampleTestTwoResultDetailTbMapper.selectAllByTwoResultIdAndConfirmStatus(bioSampleTestTwoResultTbs.get(0).getId(), "checked");
                    if (CollectionUtil.isNotEmpty(cerSampleTestBioInfoResultDetailList)) {
                        cerSampleTestBioInfoResultDetailList.forEach(cerSampleTestBioInfoResultTb -> {
                            sampleTestListDetailRspDTO.addBioInfoResultToList(cerSampleTestBioInfoResultTb.getSampleId(), cerSampleTestBioInfoResultTb.getVarType(), cerSampleTestBioInfoResultTb.getMutate(), cerSampleTestBioInfoResultTb.getRatio());
                        });
                    }
                });
            }

            sampleTestListDetailRspDTO.setMatchNum(CollectionUtil.isNotEmpty(sampleTestListDetailRspDTO.getBioInfoResultList()) ? sampleTestListDetailRspDTO.getBioInfoResultList().size() : 0);
        });
        return targetPageInfo;
    }

    @Override
    public List<BioSampleTestQuerySpeciesByApplyTypeRspDTO> querySpeciesByApplyType(String applyType) {
        List<BioSampleTestQuerySpeciesByApplyTypeRspDTO> bioSampleTestQuerySpeciesByApplyTypeRspDTOS = new ArrayList<>();
        Map<String, String> map = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        List<String> speciesCodeList = null;
        if (SampleTestApplyTypeEnum.first.name().equals(applyType)) {
            speciesCodeList = plantMultipleStockTbMapper.selectSpeciesCode();
        } else {
            speciesCodeList = plantSingleStockTbMapper.selectSpeciesCode();
        }
        if (CollectionUtil.isNotEmpty(speciesCodeList)) {
            speciesCodeList.forEach(speciesCode -> {
                BioSampleTestQuerySpeciesByApplyTypeRspDTO bioSampleTestQuerySpeciesByApplyTypeRspDTO = new BioSampleTestQuerySpeciesByApplyTypeRspDTO();
                bioSampleTestQuerySpeciesByApplyTypeRspDTO.setSpeciesCode(speciesCode);
                bioSampleTestQuerySpeciesByApplyTypeRspDTO.setSpeciesName(map.get(speciesCode));
                bioSampleTestQuerySpeciesByApplyTypeRspDTOS.add(bioSampleTestQuerySpeciesByApplyTypeRspDTO);

            });
        }
        return bioSampleTestQuerySpeciesByApplyTypeRspDTOS;
    }

    @Override
    public void downRepeatSampleTemplate(HttpServletResponse httpServletResponse) {
        ExcelUtil.writeExcel("重复取样模板", "sheet1", null, SampleTestDownRepeatSampleTemplateExcelDTO.class, httpServletResponse);
    }

    @Override
    public void downTestTemplate(DownTestTemplateReqDTO downTestTemplateReqDTO, HttpServletResponse response) {
        List<BioSampleTestResultExcelDTO> bioSampleTestResultExcelDTOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(downTestTemplateReqDTO.getApplyNo())) {
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(downTestTemplateReqDTO.getApplyNo());
            for (BioSampleTestTb bioSampleTestTb : bioSampleTestTbList) {
                BioSampleTestResultExcelDTO bioSampleTestResultExcelDTO = new BioSampleTestResultExcelDTO();
                bioSampleTestResultExcelDTO.setSampleCode(bioSampleTestTb.getSampleCode());
                bioSampleTestResultExcelDTOList.add(bioSampleTestResultExcelDTO);
            }
        }
        ExcelUtil.writeExcel("检测数据上传模板", "sheet1", bioSampleTestResultExcelDTOList, BioSampleTestResultExcelDTO.class, response);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadTestTemplate(BioSampleTestUploadTestTemplateReqDTO bioSampleTestUploadTestTemplateReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(bioSampleTestUploadTestTemplateReqDTO.getApplyNo());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行中任务");
        }
        if (!bioSampleTestUploadTestTemplateReqDTO.getExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + bioSampleTestUploadTestTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, bioSampleTestUploadTestTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<BioSampleTestResultExcelDTO> bioSampleTestResultExcelDTOList = ExcelUtil.readExcel(tempFilePath, BioSampleTestResultExcelDTO.class);
        if (CollectionUtil.isEmpty(bioSampleTestResultExcelDTOList)) {
            throw new BusinessException("无数据提交");
        }
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNoAndSampleCodeIn(bioTaskDtlTb.getTaskNum(), bioSampleTestResultExcelDTOList.stream().map(BioSampleTestResultExcelDTO::getSampleCode).collect(Collectors.toList()));
        if (bioSampleTestResultExcelDTOList.size() != bioSampleTestTbList.size()) {
            throw new BusinessException("有取样编号不存在此申请中");
        }

        List<BioSampleTestTb> updateList = new ArrayList<>();
        List<BioSampleTestOneResultTb> bioSampleSampleOneResultTbList = new ArrayList<>();
        Map<String, BioSampleTestTb> bioSampleTestTbMap = bioSampleTestTbList.stream().collect(Collectors.toMap(BioSampleTestTb::getSampleCode, bioSampleTestTb -> bioSampleTestTb));
        for (BioSampleTestResultExcelDTO bioSampleTestResultExcelDTO : bioSampleTestResultExcelDTOList) {
            log.info("检测数据上送 数据处理中：" + bioSampleTestResultExcelDTO.getSampleCode());
            BioSampleTestTb bioSampleTestTb = bioSampleTestTbMap.get(bioSampleTestResultExcelDTO.getSampleCode());
            BioSampleTestTb updateBioSampleTestTb = new BioSampleTestTb();
            updateBioSampleTestTb.setId(bioSampleTestTb.getId());
            updateBioSampleTestTb.setSampleCode(bioSampleTestTb.getSampleCode());
            updateBioSampleTestTb.setTestIdentifyPrimer(bioSampleTestResultExcelDTO.getIdentifyPrimer());
            updateBioSampleTestTb.setTestMethod(bioSampleTestResultExcelDTO.getTestMethod());
            updateBioSampleTestTb.setTestEditType(bioSampleTestResultExcelDTO.getEditType());
            updateBioSampleTestTb.setTestNoTransIdentityPrimer(bioSampleTestResultExcelDTO.getNoTransIdentityPrimer());
            updateBioSampleTestTb.setTestIsGeneModifyPositive(bioSampleTestResultExcelDTO.getIsGeneModifyPositive());
            updateBioSampleTestTb.setTestIfFixedPoint(bioSampleTestResultExcelDTO.getIfFixedPoint());
            updateBioSampleTestTb.setTestIfCopyInsert(bioSampleTestResultExcelDTO.getIfCopyInsert());
            updateBioSampleTestTb.setTestFixedPointType(bioSampleTestResultExcelDTO.getFixedPointType());
            updateBioSampleTestTb.setTestDonorResidueInfo(bioSampleTestResultExcelDTO.getDonorResidueInfo());
            updateBioSampleTestTb.setTestInsertionSite(bioSampleTestResultExcelDTO.getInsertionSite());
            updateBioSampleTestTb.setTestElisaResult(bioSampleTestResultExcelDTO.getElisaResult());
            updateBioSampleTestTb.setTestQbzrSeq(bioSampleTestResultExcelDTO.getQbzrSeq());
            updateBioSampleTestTb.setUpdateTime(new Date());
            updateBioSampleTestTb.setTestEditResidueInfo(bioSampleTestResultExcelDTO.getEditResidueInfo());
            if (updateBioSampleTestTb.ifHaveTestResult()) {
                updateBioSampleTestTb.setTestUserId(SecurityContextHolder.getUserId());
                updateBioSampleTestTb.setTestUserName(SecurityContextHolder.getNickName());
                updateBioSampleTestTb.setTestTime(DateUtil.formatDate(new Date()));
            }
            updateList.add(updateBioSampleTestTb);
            bioSampleSampleOneResultTbList.add(BioSampleTestOneResultTb.of(updateBioSampleTestTb, TestChannelEnum.project.name(), updateBioSampleTestTb.getApplyNo(), null));

        }

        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        newSampleTestDTO.setTestDataExcelUrl(bioSampleTestUploadTestTemplateReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
        //清空旧文件数据
        bioSampleTestOneResultTbMapper.deleteByUploadNum(bioSampleTestUploadTestTemplateReqDTO.getApplyNo());
        bioSampleTestResultFileTbMapper.deleteByUploadNum(bioSampleTestUploadTestTemplateReqDTO.getApplyNo());
        //插入新数据
        bioSampleTestOneResultTbMapper.insertBatch(bioSampleSampleOneResultTbList);

        BioSampleTestResultFileTb bioSampleTestResultFileTb = new BioSampleTestResultFileTb();
        bioSampleTestResultFileTb.setFileUrl(bioSampleTestUploadTestTemplateReqDTO.getExcelUrl());
        bioSampleTestResultFileTb.setResultType(CerProjectContents.TEST_ONE);
        bioSampleTestResultFileTb.setCreateUserId(SecurityContextHolder.getUserId());
        bioSampleTestResultFileTb.setCreateUserName(SecurityContextHolder.getNickName());
        bioSampleTestResultFileTb.setCreateTime(new Date());
        bioSampleTestResultFileTb.setUploadNum(bioSampleTestUploadTestTemplateReqDTO.getApplyNo());
        bioSampleTestResultFileTb.setTotalNum(bioSampleTestResultExcelDTOList.size());
        bioSampleTestResultFileTb.setEffectiveNum(bioSampleTestResultExcelDTOList.size());
        bioSampleTestResultFileTb.setNgsSuccessNum(0);
        bioSampleTestResultFileTb.setNgsFailNum(0);
        bioSampleTestResultFileTbMapper.insert(bioSampleTestResultFileTb);


        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        bioSampleTestTbMapper.updateBatchById(updateList);
    }

    @Override
    public void downIdentifyPrimerTemplate(HttpServletResponse response, String applyNo) {
        try {
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(applyNo);
            List<DownLoadIdentifyPrimerTemplateExcelDTO> downLoadIdentifyPrimerTemplateExcelDTOList = new ArrayList<DownLoadIdentifyPrimerTemplateExcelDTO>();
            for (BioSampleTestTb bioSampleTestTb : bioSampleTestTbList) {
                DownLoadIdentifyPrimerTemplateExcelDTO downLoadIdentifyPrimerTemplateExcelDTO = new DownLoadIdentifyPrimerTemplateExcelDTO();
                downLoadIdentifyPrimerTemplateExcelDTO.setSampleCode(bioSampleTestTb.getSampleCode());
                downLoadIdentifyPrimerTemplateExcelDTO.setVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
                downLoadIdentifyPrimerTemplateExcelDTOList.add(downLoadIdentifyPrimerTemplateExcelDTO);
            }
            ExcelUtil.writeExcel("下载引物模板", "sheet1", downLoadIdentifyPrimerTemplateExcelDTOList, DownLoadIdentifyPrimerTemplateExcelDTO.class, response);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("鉴定引物填写模板下载失败，请联系管理员检测模板配置");
        }
    }

    @Override
    public void uploadIdentifyPrimerTemplate(UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {

        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(uploadIdentifyPrimerTemplateReqDTO.getApplyNo());
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行中任务");
        }
        if (!uploadIdentifyPrimerTemplateReqDTO.getExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + uploadIdentifyPrimerTemplateReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, uploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【鉴定引物文件】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<DownLoadIdentifyPrimerTemplateExcelDTO> downLoadIdentifyPrimerTemplateExcelDTOList = ExcelUtil.readExcel(tempFilePath, DownLoadIdentifyPrimerTemplateExcelDTO.class);

        if (CollectionUtil.isNotEmpty(downLoadIdentifyPrimerTemplateExcelDTOList)) {
            for (DownLoadIdentifyPrimerTemplateExcelDTO downLoadIdentifyPrimerTemplateExcelDTO : downLoadIdentifyPrimerTemplateExcelDTOList) {
                BioSampleTestTb bioSampleTestTb = bioSampleTestTbMapper.selectOneByApplyNoAndSampleCode(uploadIdentifyPrimerTemplateReqDTO.getApplyNo(), downLoadIdentifyPrimerTemplateExcelDTO.getSampleCode());
                if (bioSampleTestTb != null) {
                    bioSampleTestTb.setIdentifyPrimer(downLoadIdentifyPrimerTemplateExcelDTO.getIdentifyPrimer());
                    bioSampleTestTbMapper.updateIdentifyPrimerById(downLoadIdentifyPrimerTemplateExcelDTO.getIdentifyPrimer(), bioSampleTestTb.getId());
                }
            }
        }
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        newSampleTestDTO.setIdentifyPrimerTemplateExcelUrl(uploadIdentifyPrimerTemplateReqDTO.getExcelUrl());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
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

        BioSampleLayoutTb bioSampleLayoutTb = bioSampleLayoutTbMapper.selectOneByApplyNo(applyNo);
        if (bioSampleLayoutTb == null) {
            return getLayoutPreviewRspDTO(applyNo);
        } else {
            LayoutPreviewRspDTO layoutPreviewRspDTO = new LayoutPreviewRspDTO();
            layoutPreviewRspDTO.setSingleList(JSONUtil.toList(bioSampleLayoutTb.getSingleContent(), SampleUnitDTO.class));
            JSONArray layoutListJsonArray = JSONUtil.parseArray(bioSampleLayoutTb.getPlateContent());
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
    @Transactional(rollbackFor = Exception.class)
    public void dowLayoutExcel(String applyNo, HttpServletResponse httpServletResponse) {
        BioSampleLayoutTb bioSampleLayoutTb = bioSampleLayoutTbMapper.selectOneByApplyNo(applyNo);
        List<List<List<SampleUnitDTO>>> layoutList = null;
        if (bioSampleLayoutTb != null) {
            List<SampleUnitDTO> singleSampleUnitDTOList = JSONUtil.toList(bioSampleLayoutTb.getSingleContent(), SampleUnitDTO.class);
            JSONArray layoutListJsonArray = JSONUtil.parseArray(bioSampleLayoutTb.getPlateContent());
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
        Integer totalCount = bioSampleTestTbMapper.selectCountByApplyNo(applyNo);
        Integer checkNum = bioSampleTestTbMapper.selectCountByApplyNoAndCheckResultIsNotNull(applyNo);
        countNumByApplyNoRspDTO.setCheckNum(checkNum);
        countNumByApplyNoRspDTO.setTotalNum(totalCount);
        return countNumByApplyNoRspDTO;
    }

    @Override
    public List<QueryBioInfoSampleTestResultRspDTO> queryBioInfoSampleTestResult(Integer id) {
        BioSampleTestTb bioSampleTestTb = bioSampleTestTbMapper.selectById(id);
        if (bioSampleTestTb == null) {
            throw new BusinessException("参数错误，找不到此取样信息：" + id);
        }
        List<BioSampleTestTwoResultTb> bioSampleTestTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByApplyNoAndSampleCodeOrderByIdDesc(bioSampleTestTb.getApplyNo(), bioSampleTestTb.getSampleCode());
        if (CollectionUtil.isEmpty(bioSampleTestTwoResultTbList)) {
            throw new BusinessException("没有上传NGS检测结果");
        }
        List<BioSampleTestTwoResultDetailTb> resultDetailTbs = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(bioSampleTestTwoResultTbList)) {
            Map<String, List<BioSampleTestTwoResultTb>> bioSampleTestTwoResultTbListMap = bioSampleTestTwoResultTbList.stream().collect(Collectors.groupingBy(bioSampleTestTwoResultTb -> bioSampleTestTwoResultTb.getRunId() + bioSampleTestTwoResultTb.getSampleId()));
            bioSampleTestTwoResultTbListMap.forEach((key, bioSampleTestTwoResultTbs) -> {
                List<BioSampleTestTwoResultDetailTb> cerSampleTestBioInfoResultDetailList = bioSampleTestTwoResultDetailTbMapper.selectAllByTwoResultIdAndConfirmStatus(bioSampleTestTwoResultTbs.get(0).getId(), "checked");
                if (CollectionUtil.isNotEmpty(cerSampleTestBioInfoResultDetailList)) {
                    resultDetailTbs.addAll(cerSampleTestBioInfoResultDetailList);

                }
            });
        }
        return BeanUtils.copyListProperties(resultDetailTbs, QueryBioInfoSampleTestResultRspDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synBioInfoSampleTestResult(Integer id) {
        BioSampleTestTb bioSampleTestTb = bioSampleTestTbMapper.selectById(id);
        List<BioSampleTestTwoResultTb> bioSampleSampleTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByApplyNoAndSampleCode(bioSampleTestTb.getApplyNo(), bioSampleTestTb.getSampleCode());
        if (CollectionUtil.isEmpty(bioSampleSampleTwoResultTbList)) {
            throw new BusinessException("excel没匹配到该生信检测数据");
        }
        List<BioSampleTestTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = synSampleTestResultService.synBioResult(Arrays.asList(bioSampleSampleTwoResultTbList.get(0)));
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultDetailTbList)) {
            bioSampleTestTwoResultDetailTbMapper.deleteByApplyNoAndSampleCode(bioSampleTestTb.getApplyNo(), bioSampleTestTb.getSampleCode());
            for (BioSampleTestTwoResultDetailTb cerSampleTestBioInfoResultTb : bioSampleSampleTwoResultDetailTbList) {
                bioSampleTestTwoResultDetailTbMapper.insert(cerSampleTestBioInfoResultTb);
            }
        }
        //更新结果状态
        bioSampleTestTwoResultTbMapper.updateById(bioSampleSampleTwoResultTbList.get(0));

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

        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
        Map<String, BioSampleTestTb> stringBioSampleTestTbMap = bioSampleTestTbList.stream().collect(Collectors.toMap(BioSampleTestTb::getSampleCode, bioSampleTestTb -> bioSampleTestTb));
        List<BioSampleTestTwoResultTb> bioSampleSampleTwoResultTbList = new ArrayList<>();
        List<BioSampleTestTb> updateBioSampleTestTbList = new ArrayList<>();

        //组装BioSampleSampleTwoResultTb数据
        for (SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO : sampleTestBioInfoExcelDTOList) {
            BioSampleTestTb bioSampleTestTb = stringBioSampleTestTbMap.get(sampleTestBioInfoExcelDTO.getSampleCode());

            BioSampleTestTwoResultTb bioSampleSampleTwoResultTb = new BioSampleTestTwoResultTb();
            bioSampleSampleTwoResultTb.setApplyNo(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
            bioSampleSampleTwoResultTb.setUploadNum(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
            bioSampleSampleTwoResultTb.setSampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
            bioSampleSampleTwoResultTb.setSampleId(sampleTestBioInfoExcelDTO.getSampleId());
            bioSampleSampleTwoResultTb.setRunId(sampleTestBioInfoExcelDTO.getRunId());
            bioSampleSampleTwoResultTb.setCreateTime(currentDate);
            bioSampleSampleTwoResultTb.setTestChannel(TestChannelEnum.project.name());
            if (Objects.isNull(bioSampleTestTb)) {
                //更新检测人（检测标志）
                updateBioSampleTestTbList.add(BioSampleTestTb.builder().id(bioSampleTestTb.getId()).testUserId(SecurityContextHolder.getUserId()).testUserName(SecurityContextHolder.getNickName()).build());
            } else {
                bioSampleSampleTwoResultTb.setFailMessage("取样编号错误，CER中无此取样编号");
                bioSampleSampleTwoResultTb.setSynResult(BioDrQiContents.O);
            }
            bioSampleSampleTwoResultTbList.add(bioSampleSampleTwoResultTb);


        }


        List<BioSampleTestTwoResultTb> effectiveNumBioSampleTestTwoResultTbList = bioSampleSampleTwoResultTbList.stream().filter(bioSampleTestTwoResultTb -> !BioDrQiContents.O.equals(bioSampleTestTwoResultTb.getSynResult())).collect(Collectors.toList());
        //删除旧数据
        List<BioSampleTestTwoResultTb> oldBioSampleTestTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByUploadNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(oldBioSampleTestTwoResultTbList)) {
            oldBioSampleTestTwoResultTbList.forEach(oldSampleTestTwoResultTb -> {
                bioSampleTestTwoResultDetailTbMapper.deleteByTwoResultId(oldSampleTestTwoResultTb.getId());
            });
        }
        bioSampleTestTwoResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        bioSampleTestResultFileTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        //生成新的上传记录
        BioSampleTestResultFileTb bioSampleTestResultFileTb = new BioSampleTestResultFileTb();
        bioSampleTestResultFileTb.setFileUrl(uploadBioInfoSampleTestResultReqDTO.getExcelUrl());
        bioSampleTestResultFileTb.setResultType(CerProjectContents.TEST_ONE);
        bioSampleTestResultFileTb.setCreateUserId(SecurityContextHolder.getUserId());
        bioSampleTestResultFileTb.setCreateUserName(SecurityContextHolder.getNickName());
        bioSampleTestResultFileTb.setCreateTime(new Date());
        bioSampleTestResultFileTb.setUploadNum(uploadBioInfoSampleTestResultReqDTO.getApplyNo());
        bioSampleTestResultFileTb.setTotalNum(sampleTestBioInfoExcelDTOList.size());
        bioSampleTestResultFileTb.setEffectiveNum(effectiveNumBioSampleTestTwoResultTbList.size());
        bioSampleTestResultFileTb.setNgsSuccessNum(0);
        bioSampleTestResultFileTb.setNgsFailNum(0);


        //更新检测结果
        List<BioSampleTestTwoResultDetailTb> bioSampleTwoResultDetailTbList = null;
        if (CollectionUtil.isNotEmpty(effectiveNumBioSampleTestTwoResultTbList)) {
            //异步同步结果
            bioSampleTwoResultDetailTbList = synSampleTestResultService.synBioResult(effectiveNumBioSampleTestTwoResultTbList);
            bioSampleTestResultFileTb.setNgsSuccessNum(effectiveNumBioSampleTestTwoResultTbList.stream().filter(sampleTestTwoResultTb -> BioDrQiContents.Y.equals(sampleTestTwoResultTb.getSynResult())).collect(Collectors.toList()).size());
            bioSampleTestResultFileTb.setNgsFailNum(effectiveNumBioSampleTestTwoResultTbList.stream().filter(sampleTestTwoResultTb -> BioDrQiContents.N.equals(sampleTestTwoResultTb.getSynResult())).collect(Collectors.toList()).size());
        }
        bioSampleTestResultFileTbMapper.insert(bioSampleTestResultFileTb);
        bioSampleTestTwoResultTbMapper.insertBatch(bioSampleSampleTwoResultTbList);
        bioSampleTestTbMapper.updateBatchById(updateBioSampleTestTbList);
        if (CollectionUtil.isNotEmpty(bioSampleTwoResultDetailTbList)) {
            bioSampleTestTwoResultDetailTbMapper.insertBatch(bioSampleTwoResultDetailTbList);
        }
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
    }

    @Override
    public List<CountCheckResultRspDTO> countCheckResult(String applyNo) {
        List<CountCheckResultRspDTO> list = new ArrayList<>();
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectCountNumByApplyNo(applyNo);
        if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
            bioSampleTestTbList.forEach(bioSampleTestTb -> {
                CountCheckResultRspDTO countCheckResultRspDTO = new CountCheckResultRspDTO();
                countCheckResultRspDTO.setCheckResult(bioSampleTestTb.getCheckResult());
                countCheckResultRspDTO.setCountNum(bioSampleTestTb.getCountNum() == null ? 0 : bioSampleTestTb.getCountNum());
                list.add(countCheckResultRspDTO);
            });
            return list;
        }

        return new ArrayList<>();
    }

    @Override
    public CountTestResultRspDTO countTestResult(String applyNo) {
        CountTestResultRspDTO countTestResultRspDTO = new CountTestResultRspDTO();
        countTestResultRspDTO.setCheckResultNum(bioSampleTestTbMapper.selectTestResultCount(applyNo));
        countTestResultRspDTO.setTwoResultNum(bioSampleTestTbMapper.selectTowTestResultCount(applyNo));
        countTestResultRspDTO.setNotResultNum(bioSampleTestTbMapper.selectNoTestResultCount(applyNo));
        return countTestResultRspDTO;
    }

    private LayoutPreviewRspDTO getLayoutPreviewRspDTO(String applyNo) {
        LayoutPreviewRspDTO layoutPreviewRspDTO = new LayoutPreviewRspDTO();
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(applyNo);
        if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
            return layoutPreviewRspDTO;
        }
        List<BioSampleTestTb> noIdentifyPrimerList = bioSampleTestTbList.stream().filter(bioSampleTestTb -> StringUtils.isEmpty(bioSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noIdentifyPrimerList)) {
            noIdentifyPrimerList.forEach(cerSampleTestTb -> {
                layoutPreviewRspDTO.fillSampleToSingleList(cerSampleTestTb.getVectorTaskCode(), null, cerSampleTestTb.getSampleCode(), cerSampleTestTb.getIdentifyPrimer());
            });
        }
        //96孔板
        List<BioSampleTestTb> identifyPrimerList = bioSampleTestTbList.stream().filter(bioSampleTestTb -> StringUtils.isNotEmpty(bioSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(identifyPrimerList)) {
            layoutPreviewRspDTO.setNinetySixList(SampleLayoutUtil.fillSampleToNinetySixList(identifyPrimerList));
        }

        return layoutPreviewRspDTO;
    }


    private LayoutConfirmReqDTO getLayoutConfirmReqDTO(String applyNo) {
        LayoutConfirmReqDTO layoutConfirmReqDTO = new LayoutConfirmReqDTO();
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(applyNo);
        if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
            return layoutConfirmReqDTO;
        }
        List<BioSampleTestTb> noIdentifyPrimerList = bioSampleTestTbList.stream().filter(bioSampleTestTb -> StringUtils.isEmpty(bioSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noIdentifyPrimerList)) {
            noIdentifyPrimerList.forEach(bioSampleTestTb -> {
                layoutConfirmReqDTO.fillSampleToSingleList(bioSampleTestTb.getVectorTaskCode(), null, bioSampleTestTb.getSampleCode(), bioSampleTestTb.getIdentifyPrimer());
            });
        }
        //96孔板
        List<BioSampleTestTb> identifyPrimerList = bioSampleTestTbList.stream().filter(bioSampleTestTb -> StringUtils.isNotEmpty(bioSampleTestTb.getIdentifyPrimer())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(identifyPrimerList)) {
            layoutConfirmReqDTO.setNinetySixList(SampleLayoutUtil.fillSampleToNinetySixList(identifyPrimerList));
        }
        layoutConfirmReqDTO.setApplyNo(applyNo);
        return layoutConfirmReqDTO;
    }

    @Override
    public void layoutConfirm(LayoutConfirmReqDTO layoutConfirmReqDTO) {
        BioSampleLayoutTb bioSampleLayoutTb = bioSampleLayoutTbMapper.selectOneByApplyNo(layoutConfirmReqDTO.getApplyNo());
        if (bioSampleLayoutTb != null) {
            bioSampleLayoutTb.setSingleContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getSingleList()));
            bioSampleLayoutTb.setPlateContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getNinetySixList()));
            bioSampleLayoutTb.setCreateTime(new Date());
            bioSampleLayoutTbMapper.updateById(bioSampleLayoutTb);
        } else {
            bioSampleLayoutTb = new BioSampleLayoutTb();
            bioSampleLayoutTb.setSingleContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getSingleList()));
            bioSampleLayoutTb.setPlateContent(JSONUtil.toJsonStr(layoutConfirmReqDTO.getNinetySixList()));
            bioSampleLayoutTb.setCreateTime(new Date());
            bioSampleLayoutTb.setApplyNo(layoutConfirmReqDTO.getApplyNo());
            bioSampleLayoutTbMapper.insert(bioSampleLayoutTb);
        }
    }
}
