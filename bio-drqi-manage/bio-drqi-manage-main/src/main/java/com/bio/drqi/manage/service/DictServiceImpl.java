package com.bio.drqi.manage.service;

import com.bio.cer.conf.AcceptorMaterialListRspDTO;
import com.bio.cer.conf.BreedListRspDTO;
import com.bio.cer.conf.SeedProduceAddressListRsp;
import com.bio.cer.conf.SpeciesBreedListRspDTO;
import com.bio.cer.enums.BioDictTypeEnum;
import com.bio.cer.system.rsp.DictInfoRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DictServiceImpl implements DictService, DictInnerService {
    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private CerAcceptorMaterialDictMapper cerAcceptorMaterialDictMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;


    @Override
    public List<DictInfoRspDTO> list() {
        List<BioDict> bioDictList = bioDictMapper.selectAll();
        return BeanUtils.copyListProperties(bioDictList, DictInfoRspDTO.class);
    }

    @Override
    public List<SeedProduceAddressListRsp> seedProduceAddressList() {
        List<SeedProduceAddressListRsp> list = new ArrayList<>();
        List<SeedProduceAddressDict> seedProduceAddressDictList = seedProduceAddressDictMapper.selectList(null);
        for (SeedProduceAddressDict seedProduceAddressDict : seedProduceAddressDictList) {
            SeedProduceAddressListRsp seedProduceAddressListRsp = new SeedProduceAddressListRsp();
            seedProduceAddressListRsp.setProductionLocationName(seedProduceAddressDict.getAddressName());
            seedProduceAddressListRsp.setLongitude(seedProduceAddressDict.getLongitude());
            seedProduceAddressListRsp.setLatitude(seedProduceAddressDict.getLatitude());
            list.add(seedProduceAddressListRsp);

        }
        return list;
    }

    @Override
    public List<AcceptorMaterialListRspDTO> acceptorMaterialList(String speciesCode) {
        List<AcceptorMaterialListRspDTO> list = new ArrayList<>();
        List<CerAcceptorMaterialDict> cerAcceptorMaterialDictList = cerAcceptorMaterialDictMapper.selectAllBySpeciesCode(speciesCode);
        for (CerAcceptorMaterialDict cerAcceptorMaterialDict : cerAcceptorMaterialDictList) {
            AcceptorMaterialListRspDTO acceptorMaterialListRspDTO = new AcceptorMaterialListRspDTO();
            acceptorMaterialListRspDTO.setAcceptorMaterialName(cerAcceptorMaterialDict.getAcceptorMaterialName());
            acceptorMaterialListRspDTO.setAcceptorMaterialCode(cerAcceptorMaterialDict.getAcceptorMaterialCode());
            list.add(acceptorMaterialListRspDTO);
        }
        return list;
    }

    @Override
    public List<BreedListRspDTO> breedList(String speciesCode) {
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(speciesCode);
        List<BreedListRspDTO> list = new ArrayList<>();
        for (CerBreedDict cerBreedDict : cerBreedDictList) {
            BreedListRspDTO breedListRspDTO = new BreedListRspDTO();
            breedListRspDTO.setBreedCode(cerBreedDict.getBreedCode());
            breedListRspDTO.setBreedName(cerBreedDict.getBreedName());
            list.add(breedListRspDTO);
        }
        return list;
    }

    @Override
    public List<SpeciesBreedListRspDTO> speciesBreedList() {
        List<SpeciesBreedListRspDTO> result = new ArrayList<>();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectList(null);
        for (CerSpeciesConf cerSpeciesConf : cerSpeciesConfList) {
            SpeciesBreedListRspDTO speciesBreedListRspDTO = new SpeciesBreedListRspDTO();
            speciesBreedListRspDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
            speciesBreedListRspDTO.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
            List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(cerSpeciesConf.getSpeciesCode());
            cerBreedDictList.forEach(cerBreedDict -> {
                SpeciesBreedListRspDTO.Breed breed = new SpeciesBreedListRspDTO.Breed();
                breed.setBreedCode(cerBreedDict.getBreedCode());
                breed.setBreedName(cerBreedDict.getBreedName());
                speciesBreedListRspDTO.getBreedList().add(breed);
            });
            result.add(speciesBreedListRspDTO);
        }
        return result;
    }


    @Override
    public BioDict findByDictTypeAndDictValueName(BioDictTypeEnum bioDictTypeEnum, String dictValueName) {
        if (dictValueName == null) {
            return null;
        }
        BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueName(bioDictTypeEnum.name(), dictValueName);
        if (Objects.isNull(bioDict)) {
            log.info("查询不到该字典信息：dictType={},dictValueName={}", bioDictTypeEnum.name(), dictValueName);
            throw new BusinessException("不存在该配置信息");
        }
        return bioDict;
    }

    @Override
    public BioDict findByDictTypeAndDictValueCode(BioDictTypeEnum bioDictTypeEnum, String dictValueCode) {
        if (dictValueCode == null) {
            return null;
        }
        BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueCode(bioDictTypeEnum.name(), dictValueCode);
        if (Objects.isNull(bioDict)) {
            log.info("查询不到该字典信息：dictType={},dictValueCode={}", bioDictTypeEnum.name(), dictValueCode);
            throw new BusinessException("不存在该配置信息");
        }
        return bioDict;
    }
}
