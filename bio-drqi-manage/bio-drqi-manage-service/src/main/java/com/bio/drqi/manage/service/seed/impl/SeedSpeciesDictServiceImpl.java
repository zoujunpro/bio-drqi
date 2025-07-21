package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.base.base.PageDTO;
import com.bio.base.bio.req.SpeciesAddReqDTO;
import com.bio.base.bio.req.SpeciesEditDTO;
import com.bio.base.bio.rsp.SpeciesListRspDTO;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.manage.service.seed.SeedSpeciesDictService;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SeedSpeciesDictServiceImpl implements SeedSpeciesDictService {

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public PageInfo<SpeciesListRspDTO> listPage(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        PageInfo<CerSpeciesConf> srcPageInfo = new PageInfo<>(cerSpeciesConfList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, SpeciesListRspDTO.class);
    }

    @Override
    public List<SpeciesListRspDTO> list() {
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        return BeanUtils.copyListProperties(cerSpeciesConfList, SpeciesListRspDTO.class);
    }

    @Override
    public void add(SpeciesAddReqDTO speciesAddReqDTO) {
        CerSpeciesConf cerSpeciesConf = new CerSpeciesConf();
        cerSpeciesConf.setSpeciesName(speciesAddReqDTO.getSpeciesName());
        cerSpeciesConf.setSpeciesCode(speciesAddReqDTO.getSpeciesCode());
        cerSpeciesConf.setNumPrefix(speciesAddReqDTO.getNumPrefix());
        try {
            cerSpeciesConfMapper.insert(cerSpeciesConf);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("此物种数据和已有数据冲突");
        }


    }

    @Override
    public void delete(Integer id) {
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectById(id);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(cerSpeciesConf.getSpeciesCode());
        if (CollectionUtil.isNotEmpty(cerBreedDictList)) {
            throw new BusinessException("此物种下有品种信息，无法删除");
        }
        cerSpeciesConfMapper.deleteById(id);
    }

    @Override
    public void edit(SpeciesEditDTO speciesEditDTO) {
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectById(speciesEditDTO.getId());
        cerSpeciesConf.setSpeciesName(speciesEditDTO.getSpeciesName());
        cerSpeciesConf.setSpeciesCode(speciesEditDTO.getSpeciesCode());
        cerSpeciesConf.setNumPrefix(speciesEditDTO.getNumPrefix());
        try {
            cerSpeciesConfMapper.updateById(cerSpeciesConf);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("此物种数据和已有数据冲突");
        }
    }
}
