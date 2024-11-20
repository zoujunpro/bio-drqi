package com.bio.drqi.applet.service.parse;

import cn.hutool.core.bean.BeanUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.service.parse.dto.SampleTestUniqueReqDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 取样扫码
 */
@Service
public class SampleTestCodeScanService extends AbstractBaseCodeScanService<SampleTestUniqueReqDTO, ScanCodeSampleTestRspDTO> {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Override
    SampleTestUniqueReqDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        SampleTestUniqueReqDTO sampleTestUniqueReqDTO = new SampleTestUniqueReqDTO();
        sampleTestUniqueReqDTO.setSampleCode(uniqueCodeArr[1]);
        sampleTestUniqueReqDTO.setVectorTaskCode(uniqueCodeArr[0]);
        return sampleTestUniqueReqDTO;
    }


    @Override
    ScanCodeSampleTestRspDTO dealCodeContent(SampleTestUniqueReqDTO sampleTestUniqueReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(sampleTestUniqueReqDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + sampleTestUniqueReqDTO.getVectorTaskCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByUniqueCode(cerVectorTaskTb.getProjectCode() + sampleTestUniqueReqDTO.getSampleCode());
        CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerSampleTestTb.getVectorTaskCode());
        CerVectorGroupTb cerVectorGroupTb = cerVectorGroupTbMapper.selectOneByGroupNameAndVectorTaskId(cerTransformTb.getPlasmidName(), cerVectorTaskTb.getId());

        ScanCodeSampleTestRspDTO scanCodeSampleTestRspDTO = new ScanCodeSampleTestRspDTO();
        scanCodeSampleTestRspDTO.setProjectCode(cerProjectTb.getProjectCode());
        scanCodeSampleTestRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeSampleTestRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeSampleTestRspDTO.setSubProjectName(cerSubProjectTb.getSubProjectName());
        scanCodeSampleTestRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeSampleTestRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
        scanCodeSampleTestRspDTO.setTransformName(cerVectorGroupTb.getPlasmidNames());
        scanCodeSampleTestRspDTO.setTransformCode(cerSampleTestTb.getTransformCode());
        ScanCodeSampleTestRspDTO.SampleTest sampleTest = BeanUtil.copyProperties(cerSampleTestTb, ScanCodeSampleTestRspDTO.SampleTest.class);
        scanCodeSampleTestRspDTO.setSampleTest(sampleTest);
        return scanCodeSampleTestRspDTO;
    }


}


