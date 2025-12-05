package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeProjectSampleTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.BioResultInfoDTO;
import com.bio.drqi.applet.service.codescan.dto.unique.ProjectSampleTestUniqueReqDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 取样扫码
 */
@Service
public class SampleTestCodeScanService extends AbstractBaseCodeScanService<ProjectSampleTestUniqueReqDTO, ScanCodeProjectSampleTestRspDTO> {


    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;


    @Override
    public ProjectSampleTestUniqueReqDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        ProjectSampleTestUniqueReqDTO projectSampleTestUniqueReqDTO = new ProjectSampleTestUniqueReqDTO();
        projectSampleTestUniqueReqDTO.setSampleCode(uniqueCodeArr[1]);
        projectSampleTestUniqueReqDTO.setTaskNum(uniqueCodeArr[0]);
        return projectSampleTestUniqueReqDTO;
    }


    @Override
    public ScanCodeProjectSampleTestRspDTO dealCodeContent(ProjectSampleTestUniqueReqDTO projectSampleTestUniqueReqDTO) {
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(projectSampleTestUniqueReqDTO.getSampleCode());
        if(CollectionUtil.isEmpty(bioSampleTestTbList)){
            throw new BusinessException("找不到取样编号");
        }
        BioSampleTestTb bioSampleTestTb=bioSampleTestTbList.get(0);


        ScanCodeProjectSampleTestRspDTO scanCodeProjectSampleTestRspDTO = new ScanCodeProjectSampleTestRspDTO();
        scanCodeProjectSampleTestRspDTO.setVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
        scanCodeProjectSampleTestRspDTO.setSampleCode(bioSampleTestTb.getSampleCode());
        scanCodeProjectSampleTestRspDTO.setApplyTime(bioSampleTestTb.getApplyTime());
        scanCodeProjectSampleTestRspDTO.setApplyUserId(bioSampleTestTb.getApplyUserId());
        scanCodeProjectSampleTestRspDTO.setApplyUserName(bioSampleTestTb.getApplyUserName());
        scanCodeProjectSampleTestRspDTO.setTestIdentifyPrimer(bioSampleTestTb.getTestIdentifyPrimer());
        scanCodeProjectSampleTestRspDTO.setGeneration(bioSampleTestTb.getGeneration());
        scanCodeProjectSampleTestRspDTO.setSpeciesCode(bioSampleTestTb.getSpeciesCode());
        scanCodeProjectSampleTestRspDTO.setBreedCode(bioSampleTestTb.getBreedCode());
        scanCodeProjectSampleTestRspDTO.setExperimentNum(bioSampleTestTb.getExperimentNum());
        scanCodeProjectSampleTestRspDTO.setRegionNum(bioSampleTestTb.getRegionNum());
        scanCodeProjectSampleTestRspDTO.setSeedNum(bioSampleTestTb.getSeedNum());
        scanCodeProjectSampleTestRspDTO.setTransformCode(bioSampleTestTb.getTransformCode());
        scanCodeProjectSampleTestRspDTO.setSourceCode(bioSampleTestTb.getSourceCode());
        scanCodeProjectSampleTestRspDTO.setOneTestResultInfo(BeanUtil.copyProperties(bioSampleTestTb, ScanCodeProjectSampleTestRspDTO.OneTestResultInfo.class));
        scanCodeProjectSampleTestRspDTO.setBioInfoList(queryBioInfoSampleTestResultBySampleCode(projectSampleTestUniqueReqDTO.getSampleCode()));
        return scanCodeProjectSampleTestRspDTO;
    }

    private List<BioResultInfoDTO> queryBioInfoSampleTestResultBySampleCode(String sampleCode) {
        List<BioSampleTestTwoResultTb> bioSampleTestTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllBySampleCode(sampleCode);
        if (CollectionUtil.isEmpty(bioSampleTestTwoResultTbList)) {
            return null;
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
        return BeanUtils.copyListProperties(resultDetailTbs, BioResultInfoDTO.class);
    }

}


