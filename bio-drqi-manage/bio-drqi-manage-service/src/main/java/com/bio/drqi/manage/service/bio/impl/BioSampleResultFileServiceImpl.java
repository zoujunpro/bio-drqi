package com.bio.drqi.manage.service.bio.impl.impl;

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
import com.bio.drqi.common.enums.TestChannelEnum;
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
    private CerSampleTestTbMapper cerSampleTestTbMapper;


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
        List<CerSampleTestTb> updateCerSampleTestTbList = new ArrayList<>();
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
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(testExcelDTO.getSampleCode());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    log.info("取样不存在项目管理系统：" + testExcelDTO.getSampleCode());
                    BioSampleTestOneResultTb bioSampleTestOneResultTb = new BioSampleTestOneResultTb();
                    bioSampleTestOneResultTb.setSampleCode(testExcelDTO.getSampleCode());
                    bioSampleTestOneResultTb.setUploadNum(bioSampleTestResultFileTb.getUploadNum());
                    bioSampleTestOneResultTb.setTestChannel(TestChannelEnum.project.name());
                    bioSampleTestOneResultTb.setRemark("取样编号错误或者取样编号不属于T0代取样");
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
                cerSampleTestTbList = cerSampleTestTbList.stream().sorted(Comparator.comparing(CerSampleTestTb::getId).reversed()).collect(Collectors.toList());
                //第一个的一定更新
                updateCerSampleTestTbList.add(buildUpdateCerSampleTestTb(testExcelDTO, cerSampleTestTbList.get(0).getId(), cerSampleTestTbList.get(0).getSampleCode()));
                bioSampleSampleOneResultTbList.add(BioSampleTestOneResultTb.of(buildUpdateCerSampleTestTb(testExcelDTO, cerSampleTestTbList.get(0).getId(), cerSampleTestTbList.get(0).getSampleCode()), TestChannelEnum.project.name(), cerSampleTestTbList.get(0).getApplyNo(), bioSampleTestResultFileTb.getUploadNum()));

                //剩下的，如果没有上传过结果，则补更新结果
                for (int i = 1; i < cerSampleTestTbList.size(); i++) {
                    CerSampleTestTb cerSampleTest = cerSampleTestTbList.get(i);
                    if (cerSampleTest.getTestUserId() == null) {
                        updateCerSampleTestTbList.add(buildUpdateCerSampleTestTb(testExcelDTO, cerSampleTest.getId(), cerSampleTest.getSampleCode()));
                    }
                }
                bioSampleTestResultFileTb.setEffectiveNum(bioSampleTestResultFileTb.getEffectiveNum() + 1);
            }

            if (CollectionUtil.isNotEmpty(updateCerSampleTestTbList)) {
                //更新最新的检测结果到取样数据中
                cerSampleTestTbMapper.updateBatchById(updateCerSampleTestTbList);
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
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    log.info("取样不存在项目管理系统：" + sampleTestBioInfoExcelDTO.getSampleCode());
                    BioSampleTestTwoResultTb bioSampleTestTwoResultTb = new BioSampleTestTwoResultTb();
                    bioSampleTestTwoResultTb.setSampleCode(sampleTestBioInfoExcelDTO.getSampleCode());
                    bioSampleTestTwoResultTb.setUploadNum(bioSampleTestResultFileTb.getUploadNum());
                    bioSampleTestTwoResultTb.setTestChannel(TestChannelEnum.project.name());
                    bioSampleTestTwoResultTb.setRunId(sampleTestBioInfoExcelDTO.getRunId());
                    bioSampleTestTwoResultTb.setSampleId(sampleTestBioInfoExcelDTO.getSampleId());
                    bioSampleTestTwoResultTb.setFailMessage("取样编号错误或者取样编号不属于T0代取样");
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
                cerSampleTestTbList = cerSampleTestTbList.stream().sorted(Comparator.comparing(CerSampleTestTb::getId).reversed()).collect(Collectors.toList());
                //第一个取样一定更新
                CerSampleTestTb firstCerSampleTestTb = cerSampleTestTbList.get(0);
                updateCerSampleTestTbList.add(CerSampleTestTb.builder().id(firstCerSampleTestTb.getId()).sampleCode(firstCerSampleTestTb.getSampleCode()).applyNo(firstCerSampleTestTb.getApplyNo()).testUserId(SecurityContextHolder.getUserId()).testUserName(SecurityContextHolder.getUserName()).build());

                bioSampleTwoResultTbList.add(buildBioSampleSampleTwoResultTb(sampleTestBioInfoExcelDTO, firstCerSampleTestTb, bioSampleTestResultFileTb.getUploadNum()));
                //剩下的，如果没有上传过结果，则补更新结果
                for (int i = 1; i < cerSampleTestTbList.size(); i++) {
                    CerSampleTestTb cerSampleTest = cerSampleTestTbList.get(i);
                    if (cerSampleTest.getTestUserId() == null) {
                        bioSampleTwoResultTbList.add(buildBioSampleSampleTwoResultTb(sampleTestBioInfoExcelDTO, cerSampleTest, bioSampleTestResultFileTb.getUploadNum()));
                        updateCerSampleTestTbList.add(CerSampleTestTb.builder().applyNo(cerSampleTest.getApplyNo()).sampleCode(cerSampleTest.getSampleCode()).id(cerSampleTest.getId()).testUserId(SecurityContextHolder.getUserId()).testUserName(SecurityContextHolder.getUserName()).build());

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
            updateCerSampleTestTbList = updateCerSampleTestTbList.stream().filter(cerSampleTestTb -> synNgsSuccessList.equals(cerSampleTestTb.getApplyNo() + cerSampleTestTb.getSampleCode())).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(updateCerSampleTestTbList)) {
                cerSampleTestTbMapper.updateBatchById(updateCerSampleTestTbList);
            }
        }
    }

    private BioSampleTestTwoResultTb buildBioSampleSampleTwoResultTb(SampleTestBioInfoExcelDTO sampleTestBioInfoExcelDTO, CerSampleTestTb firstCerSampleTestTb, String uploadNo) {
        BioSampleTestTwoResultTb bioSampleSampleTwoResultTb = new BioSampleTestTwoResultTb();
        bioSampleSampleTwoResultTb.setApplyNo(firstCerSampleTestTb.getApplyNo());
        bioSampleSampleTwoResultTb.setSampleCode(firstCerSampleTestTb.getSampleCode());
        bioSampleSampleTwoResultTb.setSampleId(sampleTestBioInfoExcelDTO.getSampleId());
        bioSampleSampleTwoResultTb.setRunId(sampleTestBioInfoExcelDTO.getRunId());
        bioSampleSampleTwoResultTb.setTestChannel(TestChannelEnum.project.name());
        bioSampleSampleTwoResultTb.setCreateTime(new Date());
        bioSampleSampleTwoResultTb.setUploadNum(uploadNo);
        return bioSampleSampleTwoResultTb;
    }

    @NotNull
    private static CerSampleTestTb buildUpdateCerSampleTestTb(TestExcelDTO testExcelDTO, Integer id, String sampleCode) {
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
        updateCerSampleTestTb.setTestUserName(SecurityContextHolder.getNickName());
        updateCerSampleTestTb.setTestTime(DateUtil.formatDate(new Date()));
        updateCerSampleTestTb.setUpdateTime(new Date());
        return updateCerSampleTestTb;
    }
}
