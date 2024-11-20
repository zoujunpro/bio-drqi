package com.bio.drqi.applet.service.parse;

import cn.hutool.core.bean.BeanUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.service.parse.dto.PlasmidUniqueCodeDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.domain.CerSubProjectTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.domain.CerVectorTb;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.drqi.mapper.CerSubProjectTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.CerVectorTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 质粒扫码（载体构建）
 */
@Service
public class PlasmidCodeScanService extends AbstractBaseCodeScanService<PlasmidUniqueCodeDTO, ScanCodePlasmidRspDTO> {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Override
    PlasmidUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        PlasmidUniqueCodeDTO plasmidUniqueCodeDTO = new PlasmidUniqueCodeDTO();
        plasmidUniqueCodeDTO.setPlasmidName(uniqueCodeArr[1]);
        plasmidUniqueCodeDTO.setVectorTaskCode(uniqueCodeArr[0]);
        return plasmidUniqueCodeDTO;
    }


    @Override
    ScanCodePlasmidRspDTO dealCodeContent(PlasmidUniqueCodeDTO plasmidUniqueCodeDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(plasmidUniqueCodeDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案查询不到:" + plasmidUniqueCodeDTO.getVectorTaskCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        CerVectorTb cerVectorTb = cerVectorTbMapper.selectOneByPlasmidNameAndVectorTaskId(plasmidUniqueCodeDTO.getPlasmidName(), cerVectorTaskTb.getId());
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


}


