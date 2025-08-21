package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestTransRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.TransUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 取样移苗
 */
@Service
public class SampleTransCodeScanService extends AbstractBaseCodeScanService<TransUniqueCodeDTO, ScanCodeSampleTestTransRspDTO> {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;


    @Override
    public TransUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        TransUniqueCodeDTO transUniqueCodeDTO = new TransUniqueCodeDTO();
        transUniqueCodeDTO.setSampleCode(uniqueCodeArr[2]);
        transUniqueCodeDTO.setVectorTaskCode(uniqueCodeArr[1]);
        transUniqueCodeDTO.setApplyNo(uniqueCodeArr[0]);
        return transUniqueCodeDTO;
    }

    @Override
    public ScanCodeSampleTestTransRspDTO dealCodeContent(TransUniqueCodeDTO transUniqueCodeDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(transUniqueCodeDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + transUniqueCodeDTO.getVectorTaskCode());
        }
        CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectOneByTaskNum(transUniqueCodeDTO.getApplyNo());
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByUniqueCode(cerVectorTaskTb.getProjectCode() + transUniqueCodeDTO.getSampleCode());
        CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerSampleTestTb.getVectorTaskCode());
        CerVectorGroupTb cerVectorGroupTb = cerVectorGroupTbMapper.selectOneByGroupNameAndVectorTaskId(cerTransformTb.getPlasmidName(), cerVectorTaskTb.getId());

        ScanCodeSampleTestTransRspDTO scanCodeSampleTestTransRspDTO = new ScanCodeSampleTestTransRspDTO();
        scanCodeSampleTestTransRspDTO.setProjectCode(cerProjectTb.getProjectCode());
        scanCodeSampleTestTransRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeSampleTestTransRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeSampleTestTransRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeSampleTestTransRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
        scanCodeSampleTestTransRspDTO.setTransformName(cerVectorGroupTb.getPlasmidNames());
        scanCodeSampleTestTransRspDTO.setTransformCode(cerSampleTestTb.getTransformCode());
        scanCodeSampleTestTransRspDTO.setUrls(JSONUtil.toList(cerConversionAndTransTb.getImageUrl(),String.class));

        ScanCodeSampleTestTransRspDTO.SampleTest sampleTest = BeanUtil.copyProperties(cerSampleTestTb, ScanCodeSampleTestTransRspDTO.SampleTest.class);
        scanCodeSampleTestTransRspDTO.setSampleTest(sampleTest);
        return scanCodeSampleTestTransRspDTO;
    }
}
