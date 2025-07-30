package com.bio.drqi.manage.service;

import com.bio.drqi.manage.conf.AcceptorMaterialListRspDTO;
import com.bio.drqi.manage.conf.BreedListRspDTO;
import com.bio.drqi.manage.conf.SeedProduceAddressListRsp;
import com.bio.drqi.manage.conf.SpeciesBreedListRspDTO;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.manage.system.rsp.DictInfoRspDTO;
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
