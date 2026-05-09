package com.bio.drqi.manage.service.seed.impl;

import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.domain.SeedProduceAddressDict;
import com.bio.drqi.common.dto.PageDTO;
import com.bio.drqi.manage.conf.SeedProduceAddressListRsp;
import com.bio.drqi.manage.seed.SeedProduceAddressDictAddDTO;
import com.bio.drqi.manage.seed.SeedProduceAddressDictEditDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.manage.seed.SeedProduceAddressDictListRspDTO;
import com.bio.drqi.manage.service.seed.SeedProduceAddressDictService;
import com.bio.drqi.mapper.SeedProduceAddressDictMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SeedProduceAddressDictServiceImpl implements SeedProduceAddressDictService {

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Override
    public PageInfo<SeedProduceAddressDictListRspDTO> listPage(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<SeedProduceAddressDict> seedProduceAddressDictList = pageDTO.getId() == null
                ? seedProduceAddressDictMapper.selectAll()
                : singletonList(seedProduceAddressDictMapper.selectById(pageDTO.getId()));
        PageInfo<SeedProduceAddressDict> srcPageInfo = new PageInfo<>(seedProduceAddressDictList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, SeedProduceAddressDictListRspDTO.class);
    }

    private <T> List<T> singletonList(T item) {
        return item == null ? Collections.emptyList() : Collections.singletonList(item);
    }

    @Override
    public void edit(SeedProduceAddressDictEditDTO seedProduceAddressDictEditDTO) {
        SeedProduceAddressDict seedProduceAddressDict=  seedProduceAddressDictMapper.selectById(seedProduceAddressDictEditDTO.getId());
        seedProduceAddressDict.setAddressName(seedProduceAddressDictEditDTO.getAddressName());
        seedProduceAddressDict.setLongitude(seedProduceAddressDictEditDTO.getLongitude());
        seedProduceAddressDict.setLatitude(seedProduceAddressDictEditDTO.getLatitude());
        try {
            seedProduceAddressDictMapper.updateById(seedProduceAddressDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("数据重复");
        }
    }

    @Override
    public void add(SeedProduceAddressDictAddDTO seedProduceAddressDictAddDTO) {
        SeedProduceAddressDict seedProduceAddressDict=new SeedProduceAddressDict();
        seedProduceAddressDict.setAddressName(seedProduceAddressDictAddDTO.getAddressName());
        seedProduceAddressDict.setLongitude(seedProduceAddressDictAddDTO.getLongitude());
        seedProduceAddressDict.setAddressCode(IdUtils.simpleUUID());
        seedProduceAddressDict.setLatitude(seedProduceAddressDictAddDTO.getLatitude());
        try {
            seedProduceAddressDictMapper.insert(seedProduceAddressDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("数据重复");
        }

    }

    @Override
    public void delete(Integer id) {
        seedProduceAddressDictMapper.deleteById(id);
    }

    @Override
    public List<SeedProduceAddressListRsp> list() {
        List<SeedProduceAddressListRsp> list = new ArrayList<>();
        List<SeedProduceAddressDict> seedProduceAddressDictList = seedProduceAddressDictMapper.selectList(null);
        for (SeedProduceAddressDict seedProduceAddressDict : seedProduceAddressDictList) {
            SeedProduceAddressListRsp seedProduceAddressListRsp = new SeedProduceAddressListRsp();
            seedProduceAddressListRsp.setProductionLocationName(seedProduceAddressDict.getAddressName());
            seedProduceAddressListRsp.setProductionLocationCode(seedProduceAddressDict.getAddressCode());
            seedProduceAddressListRsp.setLongitude(seedProduceAddressDict.getLongitude());
            seedProduceAddressListRsp.setLatitude(seedProduceAddressDict.getLatitude());
            list.add(seedProduceAddressListRsp);

        }
        return list;
    }


}
