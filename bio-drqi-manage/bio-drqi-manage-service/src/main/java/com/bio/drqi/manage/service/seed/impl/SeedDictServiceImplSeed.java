package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.manage.seed.SeedDictAddReqDTO;
import com.bio.drqi.manage.seed.SeedDictEditReqDTO;
import com.bio.drqi.manage.seed.SeedDictTreeListRspDTO;
import com.bio.drqi.manage.service.seed.SeedBioDictService;
import com.bio.drqi.mapper.BioDictMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeedDictServiceImplSeed implements SeedBioDictService {

    @Resource
    private BioDictMapper bioDictMapper;

    @Override
    public List<SeedDictTreeListRspDTO> list() {
        List<SeedDictTreeListRspDTO> result = new ArrayList<>();
        List<BioDict> seedDictList = bioDictMapper.selectList(null);
        Map<String, List<BioDict>> listMap = seedDictList.stream().collect(Collectors.groupingBy(BioDict::getDictType));
        listMap.forEach((dictType, list) -> {
            SeedDictTreeListRspDTO seedDictTreeListRspDTO = new SeedDictTreeListRspDTO();
            seedDictTreeListRspDTO.setDictType(dictType);
            seedDictTreeListRspDTO.setDictName(list.get(0).getDictName());
            seedDictTreeListRspDTO.setDictContentList(list.stream().map(dict -> new SeedDictTreeListRspDTO.DictContent(dict.getId(), dict.getDictValueName(), dict.getDictValueCode())).collect(Collectors.toList()));
            result.add(seedDictTreeListRspDTO);
        });
        return result;
    }

    @Override
    public void add(SeedDictAddReqDTO seedDictAddReqDTO) {
        List<BioDict> seedDictList = bioDictMapper.selectAllByDictType(seedDictAddReqDTO.getDictType());
        if (CollectionUtil.isEmpty(seedDictList)) {
            throw new BusinessException("该类型字典不存在");
        }
        List<String> valueNameList = seedDictList.stream().map(BioDict::getDictValueName).collect(Collectors.toList());
        List<String> valueCodeList = seedDictList.stream().map(BioDict::getDictValueCode).collect(Collectors.toList());

        if (valueNameList.contains(seedDictAddReqDTO.getDictValueName())) {
            throw new BusinessException("该字典值名称已经存在");
        }
        if (valueCodeList.contains(seedDictAddReqDTO.getDictValueCode())) {
            throw new BusinessException("该字典值编号已经存在");
        }
        BioDict seedDict = new BioDict();
        seedDict.setDictName(seedDictList.get(0).getDictName());
        seedDict.setDictType(seedDictAddReqDTO.getDictType());
        seedDict.setDictValueName(seedDictAddReqDTO.getDictValueName());
        seedDict.setDictValueCode(seedDictAddReqDTO.getDictValueCode());
        bioDictMapper.insert(seedDict);
    }

    @Override
    public void delete(Integer id) {
        bioDictMapper.deleteById(id);
    }

    @Override
    public void edit(SeedDictEditReqDTO seedDictEditReqDTO) {
        BioDict bioDict = bioDictMapper.selectById(seedDictEditReqDTO.getId());
        bioDict.setDictValueName(seedDictEditReqDTO.getDictValueName());
        bioDict.setDictValueCode(seedDictEditReqDTO.getDictValueCode());
        try {
            bioDictMapper.updateById(bioDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("字典值名称/编码重复");
        }
    }
}
