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
    public List<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO) {
        PageHelper.startPage(tcExperimentListPageReqDTO.getPageNum(),tcExperimentListPageReqDTO.getPageSize());
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectSelective(BeanUtils.copyProperties(tcExperimentListPageReqDTO,TcExperimentDesignTb.class));
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        Map<String, String> breedCodeOfNameMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        Map<String, String> speciesCodeOfNameMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
            List<TcExperimentListPageRspDTO> result = BeanUtils.copyListProperties(tcExperimentDesignTbList, TcExperimentListPageRspDTO.class);
            if (StringUtils.isNotEmpty(tcExperimentListPageReqDTO.getSampleApplyNum())) {
                result.forEach(obj -> {
                    List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNoAndSeedNumAndRegionNumAndCheckResult(tcExperimentListPageReqDTO.getSampleApplyNum(), obj.getSeedNum(), obj.getRegionNum(), SampleTestCheckResultEnum.stay.name());
                    obj.setStayNumber(StringUtils.isNotEmpty(bioSampleTestTbList) ? bioSampleTestTbList.size() : 0);
                    obj.setSpeciesName(speciesCodeOfNameMap.get(obj.getSpeciesCode()));
                    obj.setBreedName(breedCodeOfNameMap.get(obj.getBreedCode()));
                });
            }
            return result;

        }

        return null;
    }
}
