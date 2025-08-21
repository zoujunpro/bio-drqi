package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.SampleTestUniqueReqDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private CerSampleTestBioInfoResultTbMapper cerSampleTestBioInfoResultTbMapper;

    @Override
    public SampleTestUniqueReqDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        SampleTestUniqueReqDTO sampleTestUniqueReqDTO = new SampleTestUniqueReqDTO();
        sampleTestUniqueReqDTO.setSampleCode(uniqueCodeArr[2]);
        sampleTestUniqueReqDTO.setVectorTaskCode(uniqueCodeArr[1]);
        return sampleTestUniqueReqDTO;
    }


    @Override
    public ScanCodeSampleTestRspDTO dealCodeContent(SampleTestUniqueReqDTO sampleTestUniqueReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(sampleTestUniqueReqDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + sampleTestUniqueReqDTO.getVectorTaskCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByUniqueCode(cerVectorTaskTb.getProjectCode() + sampleTestUniqueReqDTO.getSampleCode());
        CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerSampleTestTb.getVectorTaskCode());
        CerVectorGroupTb cerVectorGroupTb = cerVectorGroupTbMapper.selectOneByGroupNameAndVectorTaskId(cerTransformTb.getPlasmidName(), cerVectorTaskTb.getId());
        List<CerSampleTestBioInfoResultTb> cerSampleTestBioInfoResultTbList = cerSampleTestBioInfoResultTbMapper.selectAllByApplyNoAndSampleCode(cerSampleTestTb.getApplyNo(), cerSampleTestTb.getSampleCode());
        ScanCodeSampleTestRspDTO scanCodeSampleTestRspDTO = new ScanCodeSampleTestRspDTO();
        if (CollectionUtil.isNotEmpty(cerSampleTestBioInfoResultTbList)) {
            List<ScanCodeSampleTestRspDTO.BioInfo> bioInfoList = BeanUtil.copyToList(cerSampleTestBioInfoResultTbList, ScanCodeSampleTestRspDTO.BioInfo.class);
            scanCodeSampleTestRspDTO.setBioInfoList(bioInfoList);
        }
        scanCodeSampleTestRspDTO.setProjectCode(cerProjectTb.getProjectCode());
        scanCodeSampleTestRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeSampleTestRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeSampleTestRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeSampleTestRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
        scanCodeSampleTestRspDTO.setTransformName(cerVectorGroupTb.getPlasmidNames());
        scanCodeSampleTestRspDTO.setTransformCode(cerSampleTestTb.getTransformCode());
        ScanCodeSampleTestRspDTO.SampleTest sampleTest = BeanUtil.copyProperties(cerSampleTestTb, ScanCodeSampleTestRspDTO.SampleTest.class);
        scanCodeSampleTestRspDTO.setSampleTest(sampleTest);
        return scanCodeSampleTestRspDTO;
    }


}


