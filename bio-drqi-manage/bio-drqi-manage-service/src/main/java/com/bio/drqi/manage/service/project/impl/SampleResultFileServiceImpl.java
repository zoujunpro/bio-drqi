package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.CerSampleApplyTb;
import com.bio.drqi.domain.CerSampleTestResultFileTb;
import com.bio.drqi.domain.CerSampleTestTb;
import com.bio.drqi.enums.SampleResultFileTypeENum;
import com.bio.drqi.manage.dto.project.NewSampleTestDTO;
import com.bio.drqi.manage.dto.project.SampleTestBioInfoExcelDTO;
import com.bio.drqi.manage.dto.project.TestExcelDTO;
import com.bio.drqi.manage.sample.req.SampleResultFileListPageReqDTO;
import com.bio.drqi.manage.sample.req.SampleResultFileUploadFileReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleResultFileListPageRspDTO;
import com.bio.drqi.manage.service.project.SampleResultFileService;
import com.bio.drqi.mapper.CerSampleTestResultFileTbMapper;
import com.bio.drqi.mapper.CerSampleTestTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SampleResultFileServiceImpl implements SampleResultFileService {

    @Resource
    private CerSampleTestResultFileTbMapper cerSampleTestResultFileTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private OssService ossService;


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
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + sampleResultFileUploadFileReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, sampleResultFileUploadFileReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【生信检测结果】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        if (SampleResultFileTypeENum.TYPE_1.code.equals(sampleResultFileUploadFileReqDTO.getResultType())) {
            List<TestExcelDTO> testExcelDTOList = ExcelUtil.readExcel(tempFilePath, TestExcelDTO.class);
            if (CollectionUtil.isNotEmpty(testExcelDTOList)) {
                for (TestExcelDTO testExcelDTO : testExcelDTOList) {
                    List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(testExcelDTO.getSampleCode());
                    if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                        throw new BusinessException("找不到此取样编号:" + testExcelDTO.getSampleCode());
                    }
                    //过滤
                    cerSampleTestTbList = cerSampleTestTbList.stream().sorted(Comparator.comparing(CerSampleTestTb::getId).reversed()).collect(Collectors.toList());
                    //第一个的一定更新
                    CerSampleTestTb cerSampleTestTb = cerSampleTestTbList.get(0);



                }

            }
        } else {
            List<SampleTestBioInfoExcelDTO> sampleTestBioInfoExcelDTOList = ExcelUtil.readExcel(tempFilePath, SampleTestBioInfoExcelDTO.class);
        }
    }
}
