package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.BioResultInfoDTO;
import com.bio.drqi.applet.service.codescan.dto.unique.SampleTestUniqueReqDTO;
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
public class SampleTestCodeScanService extends AbstractBaseCodeScanService<SampleTestUniqueReqDTO, ScanCodeSampleTestRspDTO> {


    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;


    @Override
    public SampleTestUniqueReqDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        SampleTestUniqueReqDTO sampleTestUniqueReqDTO = new SampleTestUniqueReqDTO();
        sampleTestUniqueReqDTO.setSampleCode(uniqueCodeArr[1]);
        sampleTestUniqueReqDTO.setTaskNum(uniqueCodeArr[0]);
        return sampleTestUniqueReqDTO;
    }


    @Override
    public ScanCodeSampleTestRspDTO dealCodeContent(SampleTestUniqueReqDTO sampleTestUniqueReqDTO) {
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(sampleTestUniqueReqDTO.getSampleCode());
        if(CollectionUtil.isEmpty(bioSampleTestTbList)){
            throw new BusinessException("找不到取样编号");
        }
        BioSampleTestTb bioSampleTestTb=bioSampleTestTbList.get(0);
        ScanCodeSampleTestRspDTO scanCodeSampleTestRspDTO = BeanUtil.copyProperties(bioSampleTestTb, ScanCodeSampleTestRspDTO.class);
        scanCodeSampleTestRspDTO.setOneTestResultInfo(BeanUtil.copyProperties(bioSampleTestTb, ScanCodeSampleTestRspDTO.OneTestResultInfo.class));
        scanCodeSampleTestRspDTO.setBioInfoList(queryBioInfoSampleTestResultBySampleCode(sampleTestUniqueReqDTO.getSampleCode()));
        return scanCodeSampleTestRspDTO;
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


