package com.bio.drqi.manage.service.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.SeedSourceEnum;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeedPlantService {

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;


    public void seedInStockAddRefPlant(SeedStockTb seedStockTb) {
        List<String> seedNumList = new ArrayList<>();
        //大田校验
        if (SeedSourceEnum.CODE_4.code.equals(seedStockTb.getSourceType())) {
            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber(seedStockTb.getExperimentNum(), seedStockTb.getFatherRegionNum(), seedStockTb.getMatherRegionNum(), seedStockTb.getFatherSeedNum(), seedStockTb.getMatherSeedNum(), seedStockTb.getFatherSingleNum(), seedStockTb.getMatherSingleNum());
            if (tcPollinationTb == null) {
                throw new BusinessException("回关大田种子编号异常，无此授粉信息或者授粉信息不匹配：当前对应数据行的试验方案：" + seedStockTb.getExperimentNum() + " 父本种子编号：" + seedStockTb.getFatherSeedNum() + " 母本种子编号：" + seedStockTb.getMatherSeedNum());
            }
            seedNumList = JSONUtil.toList(tcPollinationTb.getSeedNums(), String.class);
            if (!seedNumList.contains(seedStockTb.getSeedNum())) {
                seedNumList.add(seedStockTb.getSeedNum());
            }
            tcPollinationTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            tcPollinationTbMapper.updateById(tcPollinationTb);
        } else if (SeedSourceEnum.CODE_1.code.equals(seedStockTb.getSourceType())&&StringUtils.isNotEmpty(seedStockTb.getPlantCode())) {
            PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode());
            if (plantSingleStockTb == null) {
                throw new BusinessException("CER种子入库的根据种植编号找不到种植信息," + seedStockTb.getPlantCode());
            }
            seedNumList = JSONUtil.toList(plantSingleStockTb.getSeedNums(), String.class);
            if (!seedNumList.contains(seedStockTb.getSeedNum())) {
                seedNumList.add(seedStockTb.getSeedNum());
            }
            plantSingleStockTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            plantSingleStockTbMapper.updateById(plantSingleStockTb);
        }


    }

    public void seedInStockDeleteRefPlant(SeedStockTb seedStockTb) {
        List<String> seedNumList = new ArrayList<>();
        if (SeedSourceEnum.CODE_4.code.equals(seedStockTb.getSourceType())) {
            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber(seedStockTb.getExperimentNum(), seedStockTb.getFatherRegionNum(), seedStockTb.getMatherRegionNum(), seedStockTb.getFatherSeedNum(), seedStockTb.getMatherSeedNum(), seedStockTb.getFatherSingleNum(), seedStockTb.getMatherSingleNum());
            if (tcPollinationTb == null) {
                throw new BusinessException("回关大田种子编号异常，无此授粉信息或者授粉信息不匹配：当前对应数据行的试验方案：" + seedStockTb.getExperimentNum() + " 父本种子编号：" + seedStockTb.getFatherSeedNum() + " 母本种子编号：" + seedStockTb.getMatherSeedNum());
            }
            seedNumList = JSONUtil.toList(tcPollinationTb.getSeedNums(), String.class);

            if (seedNumList.contains(seedStockTb.getSeedNum())) {
                seedNumList.remove(seedStockTb.getSeedNum());
            }
            tcPollinationTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            tcPollinationTbMapper.updateById(tcPollinationTb);
        } else if (SeedSourceEnum.CODE_1.code.equals(seedStockTb.getSourceType())&&StringUtils.isNotEmpty(seedStockTb.getPlantCode())) {
            PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode());
            if (plantSingleStockTb == null) {
                throw new BusinessException("CER种子入库的根据种植编号找不到种植信息," + seedStockTb.getPlantCode());
            }
            seedNumList = JSONUtil.toList(plantSingleStockTb.getSeedNums(), String.class);
            if (!seedNumList.contains(seedStockTb.getSeedNum())) {
                seedNumList.remove(seedStockTb.getSeedNum());
            }
            plantSingleStockTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            plantSingleStockTbMapper.updateById(plantSingleStockTb);
        }
    }
}
