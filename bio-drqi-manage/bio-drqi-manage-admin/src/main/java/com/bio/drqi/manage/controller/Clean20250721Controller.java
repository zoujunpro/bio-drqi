package com.bio.drqi.manage.controller;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/testClean")
@Slf4j
public class Clean20250721Controller {

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @GetMapping("cleanBreed")
    public ResponseResult<String> cleanBreed() {
        /**
         * 清晰品种
         */
        log.info("清洗品种字典开始");
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        for (CerBreedDict cerBreedDict : cerBreedDictList) {
            cerBreedDict.setRemark(cerBreedDict.getBreedCode());
            cerBreedDict.setBreedCode(IdUtils.simpleUUID());
            cerBreedDictMapper.updateById(cerBreedDict);
        }
        log.info("清洗品种字典结束");
        log.info("清洗种子库品种开始");
        Map<String, String> breedMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getRemark(), cerBreedDict -> cerBreedDict.getBreedCode()));
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectList(null);
        for (SeedStockTb seedStockTb : seedStockTbList) {
            String breedCode = breedMap.get(seedStockTb.getSpeciesCode() + ":" + seedStockTb.getBreedCode());
            if (StringUtils.isEmpty(breedCode)) {
                throw new BusinessException("找不到品种");
            }
            seedStockTb.setBreedCode(breedCode);
            seedStockTbMapper.updateById(seedStockTb);
        }
        log.info("清洗种子库品种开始");

        return ResponseResult.getSuccess("ok");


    }
}
