package com.bio.drqi.manage.service.seed.impl;


import com.bio.base.bio.req.BreedAddReqDTO;
import com.bio.base.bio.req.BreedEditReqDTO;
import com.bio.base.bio.req.BreedListReqDTO;
import com.bio.base.bio.rsp.BreedListRspDTO;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.manage.service.seed.SeedBreedDictService;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SeedBreedDictServiceImpl implements SeedBreedDictService {

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Override
    public PageInfo<BreedListRspDTO> listPage(BreedListReqDTO breedListReqDTO) {
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectById(breedListReqDTO.getSpeciesId());
        PageHelper.startPage(breedListReqDTO.getPageNum(),breedListReqDTO.getPageSize());
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(cerSpeciesConf.getSpeciesCode());
        PageInfo<CerBreedDict> srcPageInfo=new PageInfo<>(cerBreedDictList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BreedListRspDTO.class);
    }

    @Override
    public List<BreedListRspDTO> list(Integer speciesId) {
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectById(speciesId);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(cerSpeciesConf.getSpeciesCode());
        return BeanUtils.copyListProperties(cerBreedDictList, BreedListRspDTO.class);
    }

    @Override
    public void add(BreedAddReqDTO breedAddReqDTO) {
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectById(breedAddReqDTO.getSpeciesId());
        Assert.notNull(cerSpeciesConf, "物种不存在");
        CerBreedDict cerBreedDict = new CerBreedDict();
        cerBreedDict.setBreedCode(breedAddReqDTO.getBreedCode());
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
        cerBreedDictMapper.deleteById(id);
    }

    @Override
    public void edit(BreedEditReqDTO breedEditReqDTO) {
        CerBreedDict cerBreedDict = cerBreedDictMapper.selectById(breedEditReqDTO.getId());
        cerBreedDict.setBreedCode(breedEditReqDTO.getBreedCode());
        cerBreedDict.setBreedName(breedEditReqDTO.getBreedName());
        cerBreedDictMapper.updateById(cerBreedDict);
    }
}
