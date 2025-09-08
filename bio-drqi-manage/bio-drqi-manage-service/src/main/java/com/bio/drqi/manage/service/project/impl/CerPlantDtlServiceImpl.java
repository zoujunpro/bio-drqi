package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.CerPlantDtlTb;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.manage.plant.req.PlantDtlListDetailReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListDetailRspDTO;
import com.bio.drqi.manage.service.project.CerPlantDtlService;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.CerPlantDtlTbMapper;
import com.bio.drqi.manage.plant.req.PlantDtlListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListRspDTO;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CerPlantDtlServiceImpl implements CerPlantDtlService {

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Override
    public PageInfo<PlantDtlListRspDTO> listPage(PlantDtlListReqDTO plantDtlListReqDTO) {
        PageHelper.startPage(plantDtlListReqDTO.getPageNum(), plantDtlListReqDTO.getPageSize());
        List<CerPlantDtlTb> cerPlantDtlTbList = cerPlantDtlTbMapper.selectSelective(BeanUtils.copyProperties(plantDtlListReqDTO, CerPlantDtlTb.class));
        PageInfo<CerPlantDtlTb> srcPageInfo = new PageInfo<>(cerPlantDtlTbList);
        PageInfo<PlantDtlListRspDTO> result = BeanUtils.copyPageInfoProperties(srcPageInfo, PlantDtlListRspDTO.class);
        if(CollectionUtil.isNotEmpty(result.getList())){
            List<BioDict> bioDictList = bioDictMapper.selectAll();
            Map<String, BioDict> bioDictMap = bioDictList.stream().collect(Collectors.toMap(bioDict -> bioDict.getDictType() + ":" + bioDict.getDictValueCode(), bioDict -> bioDict));
            result.getList().forEach(plantDtlListRspDTO -> {
                if(StringUtils.isNotEmpty(plantDtlListRspDTO.getPollinationMethod())){
                    BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.POLLINATE_TYPE + ":" + plantDtlListRspDTO.getPollinationMethod());
                    if (pollinationMethodBioDict == null) {
                        throw new BusinessException("授粉方式填写错误：" + plantDtlListRspDTO.getPollinationMethod());
                    }
                    plantDtlListRspDTO.setPollinationMethodName(pollinationMethodBioDict.getDictValueName());
                }
            });
        }
        return result;
    }

    @Override
    public PageInfo<PlantDtlListDetailRspDTO> listDetail(PlantDtlListDetailReqDTO plantDtlListDetailReqDTO) {
        PageHelper.startPage(plantDtlListDetailReqDTO.getPageNum(), plantDtlListDetailReqDTO.getPageSize());
        List<CerPlantDtlTb> cerPlantDtlTbList = cerPlantDtlTbMapper.selectSelective(BeanUtils.copyProperties(plantDtlListDetailReqDTO, CerPlantDtlTb.class));
        PageInfo<CerPlantDtlTb> srcPageInfo = new PageInfo<>(cerPlantDtlTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, PlantDtlListDetailRspDTO.class);
    }
}
