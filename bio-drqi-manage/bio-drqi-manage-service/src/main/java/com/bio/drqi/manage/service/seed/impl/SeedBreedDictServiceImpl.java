package com.bio.drqi.manage.service.seed.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.manage.seed.BreedAddReqDTO;
import com.bio.drqi.manage.seed.BreedEditReqDTO;
import com.bio.drqi.manage.seed.BreedListReqDTO;
import com.bio.drqi.manage.seed.BreedListRspDTO;
import com.bio.drqi.manage.service.seed.SeedBreedDictService;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class SeedBreedDictServiceImpl implements SeedBreedDictService {

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Override
    public PageInfo<BreedListRspDTO> listPage(BreedListReqDTO breedListReqDTO) {
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectById(breedListReqDTO.getSpeciesId());
        PageHelper.startPage(breedListReqDTO.getPageNum(), breedListReqDTO.getPageSize());
        List<CerBreedDict> cerBreedDictList = breedListReqDTO.getId() == null
                ? cerBreedDictMapper.selectAllBySpeciesCode(cerSpeciesConf.getSpeciesCode())
                : singletonList(cerBreedDictMapper.selectById(breedListReqDTO.getId()));
        PageInfo<CerBreedDict> srcPageInfo = new PageInfo<>(cerBreedDictList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BreedListRspDTO.class);
    }

    private <T> List<T> singletonList(T item) {
        return item == null ? Collections.emptyList() : Collections.singletonList(item);
    }

    @Override
    public List<BreedListRspDTO> list(String speciesCode) {
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(speciesCode);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(cerSpeciesConf.getSpeciesCode());
        return BeanUtils.copyListProperties(cerBreedDictList, BreedListRspDTO.class);
    }

    @Override
    public void add(BreedAddReqDTO breedAddReqDTO) {
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectById(breedAddReqDTO.getSpeciesId());
        Assert.notNull(cerSpeciesConf, "物种不存在");
        CerBreedDict cerBreedDict = new CerBreedDict();
        cerBreedDict.setBreedCode(IdUtils.simpleUUID());
        cerBreedDict.setBreedName(breedAddReqDTO.getBreedName());
        cerBreedDict.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
        try {
            cerBreedDictMapper.insert(cerBreedDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("此物种数据和已有数据冲突");
        }

    }

    @Override
    public void delete(Integer id) {
        CerBreedDict cerBreedDict = cerBreedDictMapper.selectById(id);
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectAllByBreedCode(cerBreedDict.getBreedCode());
        if (CollectionUtil.isNotEmpty(seedStockTbList)) {
            throw new BusinessException("已经使用的物种无法删除");
        }
        cerBreedDictMapper.deleteById(id);
    }

    @Override
    public void edit(BreedEditReqDTO breedEditReqDTO) {
        CerBreedDict cerBreedDict = cerBreedDictMapper.selectById(breedEditReqDTO.getId());
        cerBreedDict.setBreedName(breedEditReqDTO.getBreedName());
        cerBreedDictMapper.updateById(cerBreedDict);
    }
}
