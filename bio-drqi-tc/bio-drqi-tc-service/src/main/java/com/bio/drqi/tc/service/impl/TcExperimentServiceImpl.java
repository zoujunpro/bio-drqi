package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.tc.enums.SampleTestCheckResultEnum;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentListPageRspDTO;
import com.bio.drqi.tc.service.TcExperimentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TcExperimentServiceImpl implements TcExperimentService {

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private  BioSampleTestTbMapper bioSampleTestTbMapper;

    @Override
    public PageInfo<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO) {
        PageHelper.startPage(tcExperimentListPageReqDTO.getPageNum(),tcExperimentListPageReqDTO.getPageSize());
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectSelective(BeanUtils.copyProperties(tcExperimentListPageReqDTO,TcExperimentDesignTb.class));
        PageInfo<TcExperimentDesignTb> srcPageInfo=new PageInfo<>(tcExperimentDesignTbList);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        Map<String, String> breedCodeOfNameMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        Map<String, String> speciesCodeOfNameMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
            PageInfo<TcExperimentListPageRspDTO> result = BeanUtils.copyPageInfoProperties(srcPageInfo, TcExperimentListPageRspDTO.class);
                result.getList().forEach(tcExperimentListPageRspDTO -> {
                    tcExperimentListPageRspDTO.setSpeciesName(speciesCodeOfNameMap.get(tcExperimentListPageRspDTO.getSpeciesCode()));
                    tcExperimentListPageRspDTO.setBreedName(breedCodeOfNameMap.get(tcExperimentListPageRspDTO.getBreedCode()));
                });
            return result;

        }

        return null;
    }
}
