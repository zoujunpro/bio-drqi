package com.bio.drqi.manage.service.bio.impl;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.common.enums.TestResultEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.SampleResultFileTypeENum;
import com.bio.drqi.manage.dto.project.SampleTestBioInfoExcelDTO;
import com.bio.drqi.manage.dto.project.TestExcelDTO;
import com.bio.drqi.manage.sample.req.SampleResultFileListPageReqDTO;
import com.bio.drqi.manage.sample.req.SampleResultFileUploadFileReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleResultFileListPageRspDTO;
import com.bio.drqi.manage.service.common.SynSampleTestResultService;
import com.bio.drqi.manage.service.bio.BioSampleResultFileService;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class BioSampleResultFileServiceImpl implements BioSampleResultFileService {

    @Resource
    private BioSampleTestResultFileTbMapper bioSampleTestResultFileTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;


    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;


    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Resource
    private OssService ossService;


    @Resource
    private SynSampleTestResultService synSampleTestResultService;

    @Resource
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;


    @Override
    public PageInfo<SampleResultFileListPageRspDTO> listPage(SampleResultFileListPageReqDTO sampleResultFileListPageReqDTO) {
        PageHelper.startPage(sampleResultFileListPageReqDTO.getPageNum(), sampleResultFileListPageReqDTO.getPageSize());
        List<BioSampleTestResultFileTb> bioSampleTestResultFileTbList = bioSampleTestResultFileTbMapper.selectSelective(BeanUtils.copyProperties(sampleResultFileListPageReqDTO, BioSampleTestResultFileTb.class));
        PageInfo<BioSampleTestResultFileTb> sampleTestResultFileTbPageInfo = new PageInfo<>(bioSampleTestResultFileTbList);
        return BeanUtils.copyPageInfoProperties(sampleTestResultFileTbPageInfo, SampleResultFileListPageRspDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadFile(SampleResultFileUploadFileReqDTO sampleResultFileUploadFileReqDTO) {
        List<BioSampleTestTb> updateBioSampleTestTbList = new ArrayList<>();
        List<BioSampleTestOneResultTb> bioSampleSampleOneResultTbList = new ArrayList<>();
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + sampleResultFileUploadFileReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, sampleResultFileUploadFileReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【生信检测结果】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        //记录上传的文件
        BioSampleTestResultFileTb bioSampleTestResultFileTb = new BioSampleTestResultFileTb();
        bioSampleTestResultFileTb.setFileUrl(sampleResultFileUploadFileReqDTO.getExcelUrl());
        bioSampleTestResultFileTb.setResultType(sampleResultFileUploadFileReqDTO.getResultType());
        bioSampleTestResultFileTb.setCreateUserId(SecurityContextHolder.getUserId());
        bioSampleTestResultFileTb.setCreateUserName(SecurityContextHolder.getNickName());
        bioSampleTestResultFileTb.setCreateTime(new Date());
        bioSampleTestResultFileTb.setEffectiveNum(0);
        bioSampleTestResultFileTb.setUploadNum(DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN));
        bioSampleTestResultFileTbMapper.insert(bioSampleTestResultFileTb);


        //一代测序
        if (SampleResultFileTypeENum.TYPE_1.code.equals(sampleResultFileUploadFileReqDTO.getResultType())) {

            List<TestExcelDTO> testExcelDTOList = ExcelUtil.readExcel(tempFilePath, TestExcelDTO.class);
            if (CollectionUtil.isEmpty(testExcelDTOList)) {
                throw new BusinessException("没有读取到一代测序结果excel中数据");
            }
            for (TestExcelDTO testExcelDTO : testExcelDTOList) {
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(testExcelDTO.getSampleCode());
                if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                    log.info("取样编号错误：" + testExcelDTO.getSampleCode());
                    BioSampleTestOneResultTb bioSampleTestOneResultTb = new BioSampleTestOneResultTb();
                    bioSampleTestOneResultTb.setSampleCode(testExcelDTO.getSampleCode());
                    bioSampleTestOneResultTb.setUploadNum(bioSampleTestResultFileTb.getUploadNum());
                    bioSampleTestOneResultTb.setTestChannel(SourceCodeEnum.project.name());
                    bioSampleTestOneResultTb.setRemark("取样编号错误，取样编号在系统中未找到");
                    bioSampleSampleOneResultTbList.add(bioSampleTestOneResultTb);
                    continue;
                }
           /*     cerSampleTestTbList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> StringUtils.isEmpty(cerSampleTestTb.getCheckResult())).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    log.info("取样已经审批完成：" + testExcelDTO.getSampleCode());
                    BioSampleTestOneResultTb bioSampleTestOneResultTb=new BioSampleTestOneResultTb();
                    bioSampleTestOneResultTb.setSampleCode(testExcelDTO.getSampleCode());
                    bioSampleTestOneResultTb.setUploadNum(cerSampleTestResultFileTb.getUploadNum());
                    bioSampleTestOneResultTb.setTestChannel(TestChannelEnum.project.name());
                    bioSampleTestOneResultTb.setRemark("取样已经审批完成");
                    bioSampleSampleOneResultTbList.add(bioSampleTestOneResultTb);
                    continue;
                }*/
                bioSampleTestTbList = bioSampleTestTbList.stream().sorted(Comparator.comparing(BioSampleTestTb::getId).reversed()).collect(Collectors.toList());
                //第一个的一定更新
                updateBioSampleTestTbList.add(buildUpdateCerSampleTestTb(testExcelDTO, bioSampleTestTbList.get(0).getId(), bioSampleTestTbList.get(0).getSampleCode()));
                bioSampleSampleOneResultTbList.add(BioSampleTestOneResultTb.of(buildUpdateCerSampleTestTb(testExcelDTO, bioSampleTestTbList.get(0).getId(), bioSampleTestTbList.get(0).getSampleCode()), SourceCodeEnum.project.name(), bioSampleTestTbList.get(0).getApplyNo(), bioSampleTestResultFileTb.getUploadNum()));

                //剩下的，如果没有上传过结果，则补更新结果
                for (int i = 1; i < bioSampleTestTbList.size(); i++) {
                    BioSampleTestTb bioSampleTestTb = bioSampleTestTbList.get(i);
                    if (bioSampleTestTb.getTestUserId() == null) {
                        updateBioSampleTestTbList.add(buildUpdateCerSampleTestTb(testExcelDTO, bioSampleTestTb.getId(), bioSampleTestTb.getSampleCode()));
                    }
                }
                bioSampleTestResultFileTb.setEffectiveNum(bioSampleTestResultFileTb.getEffectiveNum() + 1);
            }

            if (CollectionUtil.isNotEmpty(updateBioSampleTestTbList)) {
                //更新最新的检测结果到取样数据中
                bioSampleTestTbMapper.updateBatchById(updateBioSampleTestTbList);
            }
            bioSampleTestResultFileTb.setTotalNum(testExcelDTOList.size());
            //更新检测结果到检测表
            bioSampleTestOneResultTbMapper.insertBatch(bioSampleSampleOneResultTbList);


            //更新文件数量和有效数量
            bioSampleTestResultFileTbMapper.updateById(bioSampleTestResultFileTb);

        }
        //NGS测序（二代测序）
        if (SampleResultFileTypeENum.TYPE_2.code.equals(sampleResultFileUploadFileReqDTO.getResultType())) {
            List<BioSampleTestTwoResultTb> bioSampleTwoResultTbList = new ArrayList<>();
            List<SampleTestBioInfoExcelDTO> sampleTestBioInfoExcelDTOList = ExcelUtil.readExcel(tempFilePath, SampleTestBioInfoExcelDTO.class);
            if (CollectionUtil.isEmpty(sampleTestBioInfoExcelDTOList)) {
                throw new BusinessException("excel中二代测序结果读取为空");
            }
            if (sampleTestBioInfoExcelDTOList.stream().map(SampleTestBioInfoExcelDTO::getSampleCode).collect(Collectors.toList()).size() != sampleTestBioInfoExcelDTOList.size()) {
                throw new BusinessException("数据文件中有重复的取样编号");
            }
            sampleTestBioInfoExcelDTOList = sampleTestBioInfoExcelDTOList.stream().filter(sampleTestBioInfoExcelDTO -> StringUtils.isNotEmpty(sampleTestBioInfoExcelDTO.getSampleId()) && StringUtils.isNotEmpty(sampleTestBioInfoExcelDTO.getRunId())).collect(Collectors.toList());
            for (SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO : sampleTestBioInfoExcelDTOList) {
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
                if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                    log.info("取样数据找不到：" + sampleTestBioInfoExcelDTO.getSampleCode());
                    BioSampleTestTwoResultTb bioSampleTestTwoResultTb = new BioSampleTestTwoResultTb();
                    bioSampleTestTwoResultTb.setSampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
                    bioSampleTestTwoResultTb.setUploadNum(bioSampleTestResultFileTb.getUploadNum());
                    bioSampleTestTwoResultTb.setTestChannel(SourceCodeEnum.project.name());
                    bioSampleTestTwoResultTb.setRunId(sampleTestBioInfoExcelDTO.getRunId());
                    bioSampleTestTwoResultTb.setSampleId(sampleTestBioInfoExcelDTO.getSampleId());
                    bioSampleTestTwoResultTb.setFailMessage("根据取样编号找不到取样数据");
                    bioSampleTestTwoResultTb.setSynResult(BioDrQiContents.O);
                    bioSampleTwoResultTbList.add(bioSampleTestTwoResultTb);
                    continue;
                }
      /*          cerSampleTestTbList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> CheckResultEnum.noCheck.name().equals(cerSampleTestTb.getCheckResult())).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    log.info("取样已经审批完成：" + sampleTestBioInfoExcelDTO.getSampleCode());
                    BioSampleTestTwoResultTb bioSampleTestOneResultTb=new BioSampleTestTwoResultTb();
                    bioSampleTestOneResultTb.setSampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
                    bioSampleTestOneResultTb.setUploadNum(cerSampleTestResultFileTb.getUploadNum());
                    bioSampleTestOneResultTb.setTestChannel(TestChannelEnum.project.name());
                    bioSampleTestOneResultTb.setRunId(sampleTestBioInfoExcelDTO.getRunId());
                    bioSampleTestOneResultTb.setSampleId(sampleTestBioInfoExcelDTO.getSampleId());
                    bioSampleTestOneResultTb.setSynResult(BioDrQiContents.O);
                    bioSampleTestOneResultTb.setFailMessage("取样已经审批完成");
                    bioSampleTwoResultTbList.add(bioSampleTestOneResultTb);
                    continue;
                }*/
                bioSampleTestTbList = bioSampleTestTbList.stream().sorted(Comparator.comparing(BioSampleTestTb::getId).reversed()).collect(Collectors.toList());
                //第一个取样一定更新
                BioSampleTestTb firstBioSampleTestTb = bioSampleTestTbList.get(0);
                updateBioSampleTestTbList.add(BioSampleTestTb.builder().id(firstBioSampleTestTb.getId()).sampleCode(firstBioSampleTestTb.getSampleCode()).applyNo(firstBioSampleTestTb.getApplyNo()).testUserId(SecurityContextHolder.getUserId()).testUserName(SecurityContextHolder.getUserName()).build());

                bioSampleTwoResultTbList.add(buildBioSampleSampleTwoResultTb(sampleTestBioInfoExcelDTO, firstBioSampleTestTb, bioSampleTestResultFileTb.getUploadNum()));
                //剩下的，如果没有上传过结果，则补更新结果
                for (int i = 1; i < bioSampleTestTbList.size(); i++) {
                    BioSampleTestTb bioSampleTestTb = bioSampleTestTbList.get(i);
                    if (bioSampleTestTb.getTestUserId() == null) {
                        bioSampleTwoResultTbList.add(buildBioSampleSampleTwoResultTb(sampleTestBioInfoExcelDTO, bioSampleTestTb, bioSampleTestResultFileTb.getUploadNum()));
                        updateBioSampleTestTbList.add(BioSampleTestTb.builder().applyNo(bioSampleTestTb.getApplyNo()).sampleCode(bioSampleTestTb.getSampleCode()).id(bioSampleTestTb.getId()).testResult(TestResultEnum.haveResult.name()).testUserId(SecurityContextHolder.getUserId()).testUserName(SecurityContextHolder.getUserName()).build());

                    }
                }
            }

            //找出文件中有效的数据
            List<BioSampleTestTwoResultTb> effectiveNumBioSampleTestTwoResultTbList = bioSampleTwoResultTbList.stream().filter(bioSampleTestTwoResultTb -> !BioDrQiContents.O.equals(bioSampleTestTwoResultTb.getSynResult())).collect(Collectors.toList());

            //更新文件中的测序信息（有效信息） 先更新生成主键ID
            bioSampleTestTwoResultTbMapper.insertBatch(bioSampleTwoResultTbList);

            //异步同步有效数据的结果并更新同步状态
            if(CollectionUtil.isNotEmpty(effectiveNumBioSampleTestTwoResultTbList)){
                List<BioSampleTestTwoResultDetailTb> bioSampleTwoResultDetailTbList = synSampleTestResultService.synBioResult(effectiveNumBioSampleTestTwoResultTbList);
                bioSampleSampleTwoResultDetailTbMapper.insertBatch(bioSampleTwoResultDetailTbList);
                effectiveNumBioSampleTestTwoResultTbList.forEach(effectiveNumBioSampleTestTwoResultTb->{
                    bioSampleTestTwoResultTbMapper.updateById(effectiveNumBioSampleTestTwoResultTb);
                });

            }
            //记录文件上传的具体信息
            bioSampleTestResultFileTb.setTotalNum(sampleTestBioInfoExcelDTOList.size());
            bioSampleTestResultFileTb.setNgsSuccessNum(bioSampleTwoResultTbList.stream().filter(sampleSampleTwoResultTb -> BioDrQiContents.Y.equals(sampleSampleTwoResultTb.getSynResult())).collect(Collectors.toList()).size());
            bioSampleTestResultFileTb.setNgsFailNum(bioSampleTwoResultTbList.stream().filter(sampleSampleTwoResultTb -> BioDrQiContents.N.equals(sampleSampleTwoResultTb.getSynResult())).collect(Collectors.toList()).size());
            bioSampleTestResultFileTb.setEffectiveNum(effectiveNumBioSampleTestTwoResultTbList.size());
            bioSampleTestResultFileTbMapper.updateById(bioSampleTestResultFileTb);
            //更新检测结果标识（取样信息上加入检测人）
            List<String> synNgsSuccessList = bioSampleTwoResultTbList.stream().filter(bioSampleSampleOneResultTb -> BioDrQiContents.Y.equals(bioSampleSampleOneResultTb.getSynResult())).map(sampleSampleTwoResultTb -> sampleSampleTwoResultTb.getApplyNo() + sampleSampleTwoResultTb.getSampleCode()).collect(Collectors.toList());
            //只有匹配成功的才认为审核成功
            updateBioSampleTestTbList = updateBioSampleTestTbList.stream().filter(bioSampleTestTb -> synNgsSuccessList.contains(bioSampleTestTb.getApplyNo() + bioSampleTestTb.getSampleCode())).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(updateBioSampleTestTbList)) {
                bioSampleTestTbMapper.updateBatchById(updateBioSampleTestTbList);
            }
        }
    }

    private BioSampleTestTwoResultTb buildBioSampleSampleTwoResultTb(SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO, BioSampleTestTb firstBioSampleTestTb, String uploadNo) {
        BioSampleTestTwoResultTb bioSampleSampleTwoResultTb = new BioSampleTestTwoResultTb();
        bioSampleSampleTwoResultTb.setApplyNo(firstBioSampleTestTb.getApplyNo());
        bioSampleSampleTwoResultTb.setSampleCode(firstBioSampleTestTb.getSampleCode());
        bioSampleSampleTwoResultTb.setSampleId(sampleTestBioInfoExcelDTO.getSampleId());
        bioSampleSampleTwoResultTb.setRunId(sampleTestBioInfoExcelDTO.getRunId());
        bioSampleSampleTwoResultTb.setTestChannel(SourceCodeEnum.project.name());
        bioSampleSampleTwoResultTb.setCreateTime(new Date());
        bioSampleSampleTwoResultTb.setUploadNum(uploadNo);
        return bioSampleSampleTwoResultTb;
    }

    @NotNull
    private static BioSampleTestTb buildUpdateCerSampleTestTb(TestExcelDTO testExcelDTO, Integer id, String sampleCode) {
        BioSampleTestTb updateBioSampleTestTb = new BioSampleTestTb();
        updateBioSampleTestTb.setId(id);
        updateBioSampleTestTb.setSampleCode(sampleCode);
        updateBioSampleTestTb.setTestIdentifyPrimer(testExcelDTO.getIdentifyPrimer());
        updateBioSampleTestTb.setTestMethod(testExcelDTO.getTestMethod());
        updateBioSampleTestTb.setTestEditType(testExcelDTO.getEditType());
        updateBioSampleTestTb.setTestNoTransIdentityPrimer(testExcelDTO.getNoTransIdentityPrimer());
        updateBioSampleTestTb.setTestIsGeneModifyPositive(testExcelDTO.getIsGeneModifyPositive());
        updateBioSampleTestTb.setTestIfFixedPoint(testExcelDTO.getIfFixedPoint());
        updateBioSampleTestTb.setTestIfCopyInsert(testExcelDTO.getIfCopyInsert());
        updateBioSampleTestTb.setTestFixedPointType(testExcelDTO.getFixedPointType());
        updateBioSampleTestTb.setTestDonorResidueInfo(testExcelDTO.getDonorResidueInfo());
        updateBioSampleTestTb.setTestInsertionSite(testExcelDTO.getInsertionSite());
        updateBioSampleTestTb.setTestElisaResult(testExcelDTO.getElisaResult());
        updateBioSampleTestTb.setTestQbzrSeq(testExcelDTO.getQbzrSeq());
        updateBioSampleTestTb.setTestEditResidueInfo(testExcelDTO.getEditResidueInfo());
        updateBioSampleTestTb.setTestUserId(SecurityContextHolder.getUserId());
        updateBioSampleTestTb.setTestUserName(SecurityContextHolder.getNickName());
        updateBioSampleTestTb.setTestTime(DateUtil.formatDate(new Date()));
        updateBioSampleTestTb.setTestResult(TestResultEnum.haveResult.name());
        updateBioSampleTestTb.setUpdateTime(new Date());
        return updateBioSampleTestTb;
    }
}
