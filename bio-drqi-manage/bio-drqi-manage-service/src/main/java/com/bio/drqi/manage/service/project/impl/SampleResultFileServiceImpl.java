package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.TestChannelEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.SampleResultFileTypeENum;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.manage.dto.project.NewSampleTestDTO;
import com.bio.drqi.manage.dto.project.SampleTestBioInfoExcelDTO;
import com.bio.drqi.manage.dto.project.TestExcelDTO;
import com.bio.drqi.manage.sample.req.SampleResultFileListPageReqDTO;
import com.bio.drqi.manage.sample.req.SampleResultFileUploadFileReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleResultFileListPageRspDTO;
import com.bio.drqi.manage.service.common.SynSampleTestResultService;
import com.bio.drqi.manage.service.project.SampleResultFileService;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SampleResultFileServiceImpl implements SampleResultFileService {

    @Resource
    private CerSampleTestResultFileTbMapper cerSampleTestResultFileTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;


    @Resource
    private CerSampleTestBioResultRefMapper cerSampleTestBioResultRefMapper;


    @Resource
    private CerSampleTestBioInfoResultTbMapper cerSampleTestBioInfoResultTbMapper;

    @Resource
    private OssService ossService;


    @Resource
    private SynSampleTestResultService synSampleTestResultService;

    @Resource
    private BioSampleSampleOneResultTbMapper bioSampleSampleOneResultTbMapper;


    @Override
    public PageInfo<SampleResultFileListPageRspDTO> listPage(SampleResultFileListPageReqDTO sampleResultFileListPageReqDTO) {
        PageHelper.startPage(sampleResultFileListPageReqDTO.getPageNum(), sampleResultFileListPageReqDTO.getPageSize());
        List<CerSampleTestResultFileTb> cerSampleTestResultFileTbList = cerSampleTestResultFileTbMapper.selectSelective(BeanUtils.copyProperties(sampleResultFileListPageReqDTO, CerSampleTestResultFileTb.class));
        PageInfo<CerSampleTestResultFileTb> sampleTestResultFileTbPageInfo = new PageInfo<>(cerSampleTestResultFileTbList);
        return BeanUtils.copyPageInfoProperties(sampleTestResultFileTbPageInfo, SampleResultFileListPageRspDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadFile(SampleResultFileUploadFileReqDTO sampleResultFileUploadFileReqDTO) {
        List<CerSampleTestTb> updateCerSampleTestTbList = new ArrayList<>();
        List<BioSampleSampleOneResultTb> bioSampleSampleOneResultTbList=new ArrayList<>();
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + sampleResultFileUploadFileReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, sampleResultFileUploadFileReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【生信检测结果】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        //记录上传的文件
        CerSampleTestResultFileTb cerSampleTestResultFileTb = new CerSampleTestResultFileTb();
        cerSampleTestResultFileTb.setFileUrl(sampleResultFileUploadFileReqDTO.getExcelUrl());
        cerSampleTestResultFileTb.setResultType(sampleResultFileUploadFileReqDTO.getResultType());
        cerSampleTestResultFileTb.setCreateUserId(SecurityContextHolder.getUserId());
        cerSampleTestResultFileTb.setCreateUserName(SecurityContextHolder.getNickName());
        cerSampleTestResultFileTb.setCreateTime(new Date());
        cerSampleTestResultFileTb.setUploadNum(DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN));
        cerSampleTestResultFileTbMapper.insert(cerSampleTestResultFileTb);

        //一代测序
        if (SampleResultFileTypeENum.TYPE_1.code.equals(sampleResultFileUploadFileReqDTO.getResultType())) {
            List<TestExcelDTO> testExcelDTOList = ExcelUtil.readExcel(tempFilePath, TestExcelDTO.class);
            if (CollectionUtil.isEmpty(testExcelDTOList)) {
                throw new BusinessException("没有读取到一代测序结果excel中数据");
            }
            for (TestExcelDTO testExcelDTO : testExcelDTOList) {
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(testExcelDTO.getSampleCode());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    log.info("取样不存在项目管理系统：" + testExcelDTO.getSampleCode());
                    continue;
                }
                cerSampleTestTbList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getCheckResult())).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    log.info("取样已经审批完成：" + testExcelDTO.getSampleCode());
                    continue;
                }
                cerSampleTestTbList = cerSampleTestTbList.stream().sorted(Comparator.comparing(CerSampleTestTb::getId).reversed()).collect(Collectors.toList());
                //第一个的一定更新
                updateCerSampleTestTbList.add(buildUpdateCerSampleTestTb(testExcelDTO, cerSampleTestTbList.get(0).getId(),cerSampleTestTbList.get(0).getSampleCode()));
                bioSampleSampleOneResultTbList.add(BioSampleSampleOneResultTb.of(buildUpdateCerSampleTestTb(testExcelDTO, cerSampleTestTbList.get(0).getId(),cerSampleTestTbList.get(0).getSampleCode()), TestChannelEnum.project.name(),null,cerSampleTestResultFileTb.getUploadNum()));

                //剩下的，如果没有上传过结果，则补更新结果
                for (int i = 1; i < cerSampleTestTbList.size(); i++) {
                    CerSampleTestTb cerSampleTest = cerSampleTestTbList.get(i);
                    if (cerSampleTest.getTestUserId() == null) {
                        updateCerSampleTestTbList.add(buildUpdateCerSampleTestTb(testExcelDTO, cerSampleTest.getId(),cerSampleTest.getSampleCode()));
                    }
                }
            }
            //更新检测结果到检测表
            bioSampleSampleOneResultTbMapper.insertBatch(bioSampleSampleOneResultTbList);
            //更新最新的检测结果到取样数据中
            cerSampleTestTbMapper.updateBatchById(updateCerSampleTestTbList);

        }
        //NGS测序（二代测序）
        if (SampleResultFileTypeENum.TYPE_2.code.equals(sampleResultFileUploadFileReqDTO.getResultType())) {
            List<CerSampleTestBioResultRef> cerSampleTestBioResultRefList = new ArrayList<>();
            List<SampleTestBioInfoExcelDTO> sampleTestBioInfoExcelDTOList = ExcelUtil.readExcel(tempFilePath, SampleTestBioInfoExcelDTO.class);
            if (CollectionUtil.isEmpty(sampleTestBioInfoExcelDTOList)) {
                throw new BusinessException("excel中二代测序结果读取为空");
            }
            sampleTestBioInfoExcelDTOList = sampleTestBioInfoExcelDTOList.stream().filter(sampleTestBioInfoExcelDTO -> StringUtils.isNotEmpty(sampleTestBioInfoExcelDTO.getSampleId()) && StringUtils.isNotEmpty(sampleTestBioInfoExcelDTO.getRunId())).collect(Collectors.toList());
            for (SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO : sampleTestBioInfoExcelDTOList) {
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    log.info("取样不存在项目管理系统：" + sampleTestBioInfoExcelDTO.getSampleCode());
                }
                cerSampleTestTbList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getCheckResult())).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    log.info("取样已经审批完成：" + sampleTestBioInfoExcelDTO.getSampleCode());
                    continue;
                }
                cerSampleTestTbList = cerSampleTestTbList.stream().sorted(Comparator.comparing(CerSampleTestTb::getId).reversed()).collect(Collectors.toList());
                //第一个取样一定更新
                CerSampleTestTb firstCerSampleTestTb = cerSampleTestTbList.get(0);
                updateCerSampleTestTbList.add(CerSampleTestTb.builder().id(firstCerSampleTestTb.getId()).testUserId(SecurityContextHolder.getUserId()).testUserName(SecurityContextHolder.getUserName()).build());
                //清空旧数据，如果有
                CerSampleTestBioResultRef cerSampleTestBioResultRef = cerSampleTestBioResultRefMapper.selectOneByApplyNoAndSampleCode(firstCerSampleTestTb.getApplyNo(), firstCerSampleTestTb.getSampleCode());
                if (cerSampleTestBioResultRef != null) {
                    cerSampleTestBioResultRefMapper.deleteById(cerSampleTestBioResultRef.getId());
                    cerSampleTestBioInfoResultTbMapper.deleteByApplyNoAndSampleCode(firstCerSampleTestTb.getApplyNo(), firstCerSampleTestTb.getSampleCode());
                }
                cerSampleTestBioResultRefList.add(buildCerSampleTestBioResultRef(sampleTestBioInfoExcelDTO, firstCerSampleTestTb, cerSampleTestResultFileTb.getUploadNum()));
                //剩下的，如果没有上传过结果，则补更新结果
                for (int i = 1; i < cerSampleTestTbList.size(); i++) {
                    CerSampleTestTb cerSampleTest = cerSampleTestTbList.get(i);
                    if (cerSampleTest.getTestUserId() == null) {
                        cerSampleTestBioResultRefList.add(buildCerSampleTestBioResultRef(sampleTestBioInfoExcelDTO, cerSampleTest, cerSampleTestResultFileTb.getUploadNum()));
                        updateCerSampleTestTbList.add(CerSampleTestTb.builder().id(cerSampleTest.getId()).testUserId(SecurityContextHolder.getUserId()).testUserName(SecurityContextHolder.getUserName()).build());

                    }
                }
            }
            //更新文件中的测序信息（有效信息）
            cerSampleTestBioResultRefMapper.insertBatch(cerSampleTestBioResultRefList);
            //更新检测结果标识（取样信息上加入检测人）
            cerSampleTestTbMapper.updateBatchById(updateCerSampleTestTbList);
            //异步同步结果
            List<CerSampleTestBioInfoResultTb> cerSampleTestBioInfoResultTbList = synSampleTestResultService.synBioResult(cerSampleTestBioResultRefList);
            //更新同步的结果，更新前旧的需要删除
            if (CollectionUtil.isNotEmpty(cerSampleTestBioInfoResultTbList)) {
                cerSampleTestBioInfoResultTbMapper.insertBatch(cerSampleTestBioInfoResultTbList);
            }
        }
    }

    private CerSampleTestBioResultRef buildCerSampleTestBioResultRef(SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO, CerSampleTestTb firstCerSampleTestTb, String uploadNo) {
        CerSampleTestBioResultRef updateCerSampleTestBioResultRef = new CerSampleTestBioResultRef();
        updateCerSampleTestBioResultRef.setApplyNo(firstCerSampleTestTb.getApplyNo());
        updateCerSampleTestBioResultRef.setSampleCode(firstCerSampleTestTb.getSampleCode());
        updateCerSampleTestBioResultRef.setVectorTaskCode(firstCerSampleTestTb.getVectorTaskCode());
        updateCerSampleTestBioResultRef.setSampleId(sampleTestBioInfoExcelDTO.getSampleId());
        updateCerSampleTestBioResultRef.setRunId(sampleTestBioInfoExcelDTO.getRunId());
        updateCerSampleTestBioResultRef.setCreateTime(new Date());
        updateCerSampleTestBioResultRef.setUploadNum(uploadNo);
        return updateCerSampleTestBioResultRef;
    }

    @NotNull
    private static CerSampleTestTb buildUpdateCerSampleTestTb(TestExcelDTO testExcelDTO, Integer id,String sampleCode) {
        CerSampleTestTb updateCerSampleTestTb = new CerSampleTestTb();
        updateCerSampleTestTb.setId(id);
        updateCerSampleTestTb.setSampleCode(sampleCode);
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
        return updateCerSampleTestTb;
    }
}
