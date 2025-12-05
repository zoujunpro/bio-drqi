package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformTransRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.unique.TransUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 取样移苗
 */
@Service
public class transformTransCodeScanService extends AbstractBaseCodeScanService<TransUniqueCodeDTO, ScanCodeTransformTransRspDTO> {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;


    @Override
    public TransUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        TransUniqueCodeDTO transUniqueCodeDTO = new TransUniqueCodeDTO();
        transUniqueCodeDTO.setTransformCode(uniqueCodeArr[2]);
        transUniqueCodeDTO.setVectorTaskCode(uniqueCodeArr[1]);
        transUniqueCodeDTO.setApplyNo(uniqueCodeArr[0]);
        return transUniqueCodeDTO;
    }

    @Override
    public ScanCodeTransformTransRspDTO dealCodeContent(TransUniqueCodeDTO transUniqueCodeDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(transUniqueCodeDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + transUniqueCodeDTO.getVectorTaskCode());
        }

        CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectOneByTaskNum(transUniqueCodeDTO.getApplyNo());
        if (cerConversionAndTransTb == null) {
            throw new BusinessException("无此移苗数据");
        }
        CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(transUniqueCodeDTO.getTransformCode(), transUniqueCodeDTO.getVectorTaskCode());
        if (cerTransformTb == null) {
            throw new BusinessException("转化编号非法：" + cerTransformTb.getTransformCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        ScanCodeTransformTransRspDTO scanCodeTransformRspDTO = new ScanCodeTransformTransRspDTO();
        scanCodeTransformRspDTO.setProjectCode(cerVectorTaskTb.getProjectCode());
        scanCodeTransformRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeTransformRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeTransformRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeTransformRspDTO.setUrls(JSONUtil.toList(cerConversionAndTransTb.getImageUrl(), String.class));
        ScanCodeTransformTransRspDTO.Transform transform = BeanUtil.copyProperties(cerTransformTb, ScanCodeTransformTransRspDTO.Transform.class);
        scanCodeTransformRspDTO.setTransform(transform);
        return scanCodeTransformRspDTO;
    }
}
