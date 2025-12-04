package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.TissueEmbryoUniqueReqDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TissueEmbryoCodeScanService extends AbstractBaseCodeScanService<TissueEmbryoUniqueReqDTO, ScanCodeSampleTestRspDTO> {

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
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Override
    public TissueEmbryoUniqueReqDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        TissueEmbryoUniqueReqDTO tissueEmbryoUniqueReqDTO = new TissueEmbryoUniqueReqDTO();
        tissueEmbryoUniqueReqDTO.setSampleCode(uniqueCodeArr[2]);
        tissueEmbryoUniqueReqDTO.setVectorTaskCode(uniqueCodeArr[1]);
        return tissueEmbryoUniqueReqDTO;
    }

    @Override
    public ScanCodeSampleTestRspDTO dealCodeContent(TissueEmbryoUniqueReqDTO tissueEmbryoUniqueReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(tissueEmbryoUniqueReqDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + tissueEmbryoUniqueReqDTO.getVectorTaskCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(tissueEmbryoUniqueReqDTO.getSampleCode());
        CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(bioSampleTestTbList.get(0).getTransformCode(), bioSampleTestTbList.get(0).getVectorTaskCode());
        CerVectorGroupTb cerVectorGroupTb = cerVectorGroupTbMapper.selectOneByGroupNameAndVectorTaskId(cerTransformTb.getPlasmidName(), cerVectorTaskTb.getId());
        List<BioSampleTestTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = bioSampleSampleTwoResultDetailTbMapper.selectAllByApplyNoAndSampleCode(bioSampleTestTbList.get(0).getApplyNo(), bioSampleTestTbList.get(0).getSampleCode());
        ScanCodeSampleTestRspDTO scanCodeSampleTestRspDTO = new ScanCodeSampleTestRspDTO();
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultDetailTbList)) {
            List<ScanCodeSampleTestRspDTO.BioInfo> bioInfoList = BeanUtil.copyToList(bioSampleSampleTwoResultDetailTbList, ScanCodeSampleTestRspDTO.BioInfo.class);
            scanCodeSampleTestRspDTO.setBioInfoList(bioInfoList);
        }
        scanCodeSampleTestRspDTO.setProjectCode(cerProjectTb.getProjectCode());
        scanCodeSampleTestRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeSampleTestRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeSampleTestRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeSampleTestRspDTO.setTransformName(cerVectorGroupTb.getPlasmidNames());
        scanCodeSampleTestRspDTO.setTransformCode(bioSampleTestTbList.get(0).getTransformCode());
        ScanCodeSampleTestRspDTO.SampleTest sampleTest = BeanUtil.copyProperties(bioSampleTestTbList.get(0), ScanCodeSampleTestRspDTO.SampleTest.class);
        scanCodeSampleTestRspDTO.setSampleTest(sampleTest);
        return scanCodeSampleTestRspDTO;
    }
}
