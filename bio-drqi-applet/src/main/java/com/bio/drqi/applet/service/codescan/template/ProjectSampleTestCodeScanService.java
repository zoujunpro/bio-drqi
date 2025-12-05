package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodeProjectSampleTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.ProjectSampleTestUniqueReqDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 取样扫码
 */
@Service
public class ProjectSampleTestCodeScanService extends AbstractBaseCodeScanService<ProjectSampleTestUniqueReqDTO, ScanCodeProjectSampleTestRspDTO> {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Override
    public ProjectSampleTestUniqueReqDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        ProjectSampleTestUniqueReqDTO projectSampleTestUniqueReqDTO = new ProjectSampleTestUniqueReqDTO();
        projectSampleTestUniqueReqDTO.setSampleCode(uniqueCodeArr[2]);
        projectSampleTestUniqueReqDTO.setVectorTaskCode(uniqueCodeArr[1]);
        return projectSampleTestUniqueReqDTO;
    }


    @Override
    public ScanCodeProjectSampleTestRspDTO dealCodeContent(ProjectSampleTestUniqueReqDTO projectSampleTestUniqueReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(projectSampleTestUniqueReqDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + projectSampleTestUniqueReqDTO.getVectorTaskCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode( projectSampleTestUniqueReqDTO.getSampleCode());
        CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(bioSampleTestTbList.get(0).getTransformCode(), bioSampleTestTbList.get(0).getVectorTaskCode());
        List<BioSampleTestTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = bioSampleSampleTwoResultDetailTbMapper.selectAllByApplyNoAndSampleCode(bioSampleTestTbList.get(0).getApplyNo(), bioSampleTestTbList.get(0).getSampleCode());
        ScanCodeProjectSampleTestRspDTO scanCodeProjectSampleTestRspDTO = new ScanCodeProjectSampleTestRspDTO();
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultDetailTbList)) {
            List<ScanCodeProjectSampleTestRspDTO.BioInfo> bioInfoList = BeanUtil.copyToList(bioSampleSampleTwoResultDetailTbList, ScanCodeProjectSampleTestRspDTO.BioInfo.class);
            scanCodeProjectSampleTestRspDTO.setBioInfoList(bioInfoList);
        }
        scanCodeProjectSampleTestRspDTO.setProjectCode(cerProjectTb.getProjectCode());
        scanCodeProjectSampleTestRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeProjectSampleTestRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeProjectSampleTestRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeProjectSampleTestRspDTO.setTransformName(cerTransformTb.getTransformCode());
        scanCodeProjectSampleTestRspDTO.setTransformCode(bioSampleTestTbList.get(0).getTransformCode());
        ScanCodeProjectSampleTestRspDTO.SampleTest sampleTest = BeanUtil.copyProperties(bioSampleTestTbList.get(0), ScanCodeProjectSampleTestRspDTO.SampleTest.class);
        scanCodeProjectSampleTestRspDTO.setSampleTest(sampleTest);
        return scanCodeProjectSampleTestRspDTO;
    }


}


