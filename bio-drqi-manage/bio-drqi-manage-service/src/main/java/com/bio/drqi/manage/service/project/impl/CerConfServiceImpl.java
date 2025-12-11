package com.bio.drqi.manage.service.project.impl;

import com.bio.drqi.manage.conf.SpeciesConfRspDTO;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.manage.service.project.CerConfService;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CerConfServiceImpl implements CerConfService {

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;
    @Override
    public List<SpeciesConfRspDTO> speciesList() {
        List<SpeciesConfRspDTO> speciesConfRspDTOList = new ArrayList<>();
        List<CerSpeciesConf> speciesConfList = cerSpeciesConfMapper.selectList(null);
        for (CerSpeciesConf cerSpeciesConf : speciesConfList) {
            SpeciesConfRspDTO speciesConfRspDTO = new SpeciesConfRspDTO();
            speciesConfRspDTO.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
            speciesConfRspDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
            speciesConfRspDTOList.add(speciesConfRspDTO);
        }
        return speciesConfRspDTOList;
    }


}
