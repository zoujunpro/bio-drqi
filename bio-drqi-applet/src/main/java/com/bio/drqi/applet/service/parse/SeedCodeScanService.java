package com.bio.drqi.applet.service.parse;

import cn.hutool.core.bean.BeanUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;
import com.bio.drqi.applet.service.parse.dto.TransformUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 转化扫码
 */
@Service
public class SeedCodeScanService extends AbstractBaseCodeScanService<TransformUniqueCodeDTO, ScanCodeTransformRspDTO> {

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

    @Override
    TransformUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        TransformUniqueCodeDTO transformUniqueCodeDTO = new TransformUniqueCodeDTO();
        transformUniqueCodeDTO.setTransformCode(uniqueCodeArr[1]);
        transformUniqueCodeDTO.setVectorTaskCode(uniqueCodeArr[0]);
        return transformUniqueCodeDTO;
    }


    @Override
    ScanCodeTransformRspDTO dealCodeContent(TransformUniqueCodeDTO transformUniqueCodeDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(transformUniqueCodeDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + transformUniqueCodeDTO.getVectorTaskCode());
        }
        CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(transformUniqueCodeDTO.getTransformCode(), transformUniqueCodeDTO.getVectorTaskCode());
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


}


