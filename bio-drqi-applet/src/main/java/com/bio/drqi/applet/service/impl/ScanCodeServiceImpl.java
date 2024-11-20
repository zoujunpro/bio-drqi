package com.bio.drqi.applet.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.SpringUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.applet.service.parse.dto.ParseCodePlasmidDTO;
import com.bio.drqi.applet.dto.req.ScanCodeSampleTestReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeTransformReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;
import com.bio.drqi.applet.service.ScanCodeService;
import com.bio.drqi.applet.service.parse.BaseCodeParse;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.print.api.PrintApi;
import com.bio.print.rsp.PrintDataRspDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ScanCodeServiceImpl implements ScanCodeService {

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private PrintApi printApi;


    @Override
    public Object scanCode(String code) {
        ResponseResult<PrintDataRspDTO> responseResult = printApi.queryPrintDataByCode(code);
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        PrintDataRspDTO printDataRspDTO = responseResult.getData();
        if (StringUtils.isEmpty(printDataRspDTO.getUniqueCode())) {
            throw new BusinessException("二维码异常，请联系管理员：" + code);
        }
        BaseCodeParse baseCodeParse = SpringUtils.getBean(printDataRspDTO.getPrintType());
        return baseCodeParse.doScan(printDataRspDTO.getUniqueCode());
    }


    @Override
    public ScanCodePlasmidRspDTO plasmidDetail(ParseCodePlasmidDTO parseCodePlasmidDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(parseCodePlasmidDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + parseCodePlasmidDTO.getVectorTaskCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        CerVectorTb cerVectorTb = cerVectorTbMapper.selectOneByPlasmidNameAndVectorTaskId(parseCodePlasmidDTO.getPlasmidName(), cerVectorTaskTb.getId());
        ScanCodePlasmidRspDTO scanCodePlasmidRspDTO = new ScanCodePlasmidRspDTO();
        scanCodePlasmidRspDTO.setProjectCode(cerVectorTaskTb.getProjectCode());
        scanCodePlasmidRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodePlasmidRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodePlasmidRspDTO.setSubProjectName(cerSubProjectTb.getSubProjectName());
        scanCodePlasmidRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodePlasmidRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
        ScanCodePlasmidRspDTO.CerVector cerVector = BeanUtil.copyProperties(cerVectorTb, ScanCodePlasmidRspDTO.CerVector.class);
        scanCodePlasmidRspDTO.setCerVector(cerVector);
        return scanCodePlasmidRspDTO;
    }

    @Override
    public ScanCodeTransformRspDTO transform(ScanCodeTransformReqDTO scanCodeTransformReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(scanCodeTransformReqDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + scanCodeTransformReqDTO.getVectorTaskCode());
        }
        CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(scanCodeTransformReqDTO.getTransformCode(), scanCodeTransformReqDTO.getVectorTaskCode());
        if (cerTransformTb == null) {
            throw new BusinessException("转化编号非法：" + cerTransformTb.getTransformCode());
        }
        CerVectorGroupTb cerVectorGroupTb = cerVectorGroupTbMapper.selectOneByGroupNameAndVectorTaskId(cerTransformTb.getPlasmidName(), cerVectorTaskTb.getId());
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());

        ScanCodeTransformRspDTO scanCodeTransformRspDTO = new ScanCodeTransformRspDTO();
        scanCodeTransformRspDTO.setProjectCode(cerVectorTaskTb.getProjectCode());
        scanCodeTransformRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeTransformRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeTransformRspDTO.setSubProjectName(cerSubProjectTb.getSubProjectName());
        scanCodeTransformRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeTransformRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
        ScanCodeTransformRspDTO.Transform transform = BeanUtil.copyProperties(cerTransformTb, ScanCodeTransformRspDTO.Transform.class);
        transform.setTransformName(cerVectorGroupTb.getGroupName());
        transform.setPlasmidName(cerVectorGroupTb.getPlasmidNames());
        scanCodeTransformRspDTO.setTransform(transform);
        return scanCodeTransformRspDTO;
    }

    @Override
    public ScanCodeSampleTestRspDTO sampleTest(ScanCodeSampleTestReqDTO scanCodeSampleTestReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(scanCodeSampleTestReqDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + scanCodeSampleTestReqDTO.getVectorTaskCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByUniqueCode(cerVectorTaskTb.getProjectCode() + scanCodeSampleTestReqDTO.getSampleCode());
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
