package com.bio.drqi.manage.service.project.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerPlantDtlTb;
import com.bio.drqi.manage.service.project.CerPlantDtlService;
import com.bio.drqi.mapper.CerPlantDtlTbMapper;
import com.bio.drqi.plant.req.PlantDtlListReqDTO;
import com.bio.drqi.plant.rsp.PlantDtlListRspDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CerPlantDtlServiceImpl implements CerPlantDtlService {

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Override
    public PageInfo<PlantDtlListRspDTO> listPage(PlantDtlListReqDTO plantDtlListReqDTO) {
        PageHelper.startPage(plantDtlListReqDTO.getPageNum(), plantDtlListReqDTO.getPageSize());
        List<CerPlantDtlTb> cerPlantDtlTbList = cerPlantDtlTbMapper.selectSelective(null);
        PageInfo<CerPlantDtlTb> srcPageInfo = new PageInfo<>(cerPlantDtlTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, PlantDtlListRspDTO.class);
    }
}
