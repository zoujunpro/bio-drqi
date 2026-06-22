package com.bio.drqi.manage.service.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.domain.TcHarvestSeedTb;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import com.bio.drqi.mapper.TcHarvestSeedTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SeedPlantService {

    private static final String TC_HARVEST_SEED_UNIQUE_CODE_PREFIX = "tc_harvest_seed_";

    @Resource
    private TcHarvestSeedTbMapper tcHarvestSeedTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;


    public void seedInStockAddRefPlant(SeedStockTb seedStockTb) {
        seedInStockAddRefPlant(seedStockTb, null);
    }

    public void seedInStockAddRefPlant(SeedStockTb seedStockTb, String uniqueCode) {
        TcHarvestSeedTb currentTcHarvestSeedTb = null;
        //大田校验
        if (SeedSourceEnum.CODE_4.code.equals(seedStockTb.getSourceType())) {
            currentTcHarvestSeedTb = selectTcHarvestSeed(seedStockTb, uniqueCode);
            List<String> seedNumList = JSONUtil.toList(currentTcHarvestSeedTb.getSeedNums(), String.class);

            if (!seedNumList.contains(seedStockTb.getSeedNum())) {
                seedNumList.add(seedStockTb.getSeedNum());
            }
            currentTcHarvestSeedTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            tcHarvestSeedTbMapper.updateById(currentTcHarvestSeedTb);
        } else if (SeedSourceEnum.CODE_1.code.equals(seedStockTb.getSourceType()) && StringUtils.isNotEmpty(seedStockTb.getPlantCode())) {
            PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode());
            if (plantSingleStockTb == null) {
                throw new BusinessException("CER种子入库的根据种植编号找不到种植信息," + seedStockTb.getPlantCode());
            }
            List<String> seedNumList = JSONUtil.toList(plantSingleStockTb.getSeedNums(), String.class);
            if (!seedNumList.contains(seedStockTb.getSeedNum())) {
                seedNumList.add(seedStockTb.getSeedNum());
            }
            plantSingleStockTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            plantSingleStockTbMapper.updateById(plantSingleStockTb);
        }
    }

    public void seedInStockDeleteRefPlant(SeedStockTb seedStockTb) {
        seedInStockDeleteRefPlant(seedStockTb, null);
    }

    public void seedInStockDeleteRefPlant(SeedStockTb seedStockTb, String uniqueCode) {
        if (SeedSourceEnum.CODE_4.code.equals(seedStockTb.getSourceType())) {
            deleteTcHarvestSeedNum(seedStockTb, uniqueCode);
        } else if (SeedSourceEnum.CODE_1.code.equals(seedStockTb.getSourceType()) && StringUtils.isNotEmpty(seedStockTb.getPlantCode())) {
            deletePlantSeedNum(seedStockTb);
        }
    }

    private void deleteTcHarvestSeedNum(SeedStockTb seedStockTb, String uniqueCode) {
        TcHarvestSeedTb tcHarvestSeedTb = selectTcHarvestSeedByUniqueCode(uniqueCode);
        if (tcHarvestSeedTb != null) {
            removeSeedNumAndUpdate(tcHarvestSeedTb, seedStockTb.getSeedNum());
            return;
        }
        List<TcHarvestSeedTb> tcHarvestSeedTbList = tcHarvestSeedTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber(seedStockTb.getExperimentNum(), seedStockTb.getFatherRegionNum(), seedStockTb.getMatherRegionNum(), seedStockTb.getFatherSeedNum(), seedStockTb.getMatherSeedNum(), seedStockTb.getFatherSingleNum(), seedStockTb.getMatherSingleNum());
        if (CollectionUtil.isEmpty(tcHarvestSeedTbList)) {
            return;
        }
        for (TcHarvestSeedTb item : tcHarvestSeedTbList) {
            removeSeedNumAndUpdate(item, seedStockTb.getSeedNum());
        }
    }

    private void deletePlantSeedNum(SeedStockTb seedStockTb) {
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode());
        if (plantSingleStockTb == null) {
            throw new BusinessException("CER种子入库的根据种植编号找不到种植信息," + seedStockTb.getPlantCode());
        }
        List<String> seedNumList = JSONUtil.toList(plantSingleStockTb.getSeedNums(), String.class);
        if (seedNumList.contains(seedStockTb.getSeedNum())) {
            seedNumList.remove(seedStockTb.getSeedNum());
            plantSingleStockTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            plantSingleStockTbMapper.updateById(plantSingleStockTb);
        }
    }

    private void removeSeedNumAndUpdate(TcHarvestSeedTb tcHarvestSeedTb, String seedNum) {
        List<String> seedNumList = JSONUtil.toList(tcHarvestSeedTb.getSeedNums(), String.class);
        if (!seedNumList.contains(seedNum)) {
            return;
        }
        seedNumList.remove(seedNum);
        tcHarvestSeedTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
        tcHarvestSeedTbMapper.updateById(tcHarvestSeedTb);
    }

    private TcHarvestSeedTb selectTcHarvestSeed(SeedStockTb seedStockTb, String uniqueCode) {
        TcHarvestSeedTb tcHarvestSeedTb = selectTcHarvestSeedByUniqueCode(uniqueCode);
        if (tcHarvestSeedTb != null) {
            return tcHarvestSeedTb;
        }
        List<TcHarvestSeedTb> tcHarvestSeedTbList = tcHarvestSeedTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber(seedStockTb.getExperimentNum(), seedStockTb.getFatherRegionNum(), seedStockTb.getMatherRegionNum(), seedStockTb.getFatherSeedNum(), seedStockTb.getMatherSeedNum(), seedStockTb.getFatherSingleNum(), seedStockTb.getMatherSingleNum());
        if (CollectionUtil.isEmpty(tcHarvestSeedTbList)) {
            throw new BusinessException("回关大田种子编号异常，无此授粉信息或者授粉信息不匹配：当前对应数据行的试验方案：" + seedStockTb.getExperimentNum() + " 父本种子编号：" + seedStockTb.getFatherSeedNum() + " 母本种子编号：" + seedStockTb.getMatherSeedNum());
        }
        if (tcHarvestSeedTbList.size() == 1) {
            return tcHarvestSeedTbList.get(0);
        }
        for (TcHarvestSeedTb item : tcHarvestSeedTbList) {
            if (StringUtils.equals(item.getRemark(), seedStockTb.getRemarks()) && StringUtils.equals(seedStockTb.getHarvestTime(), item.getHarvestTime())) {
                return item;
            }
            if (StringUtils.equals(item.getRemark(), seedStockTb.getRemarks())) {
                return item;
            }
            if (CollectionUtil.isEmpty(JSONUtil.toList(item.getSeedNums(), String.class))) {
                return item;
            }
        }
        return tcHarvestSeedTbList.get(0);
    }

    private TcHarvestSeedTb selectTcHarvestSeedByUniqueCode(String uniqueCode) {
        if (StringUtils.isEmpty(uniqueCode) || !uniqueCode.startsWith(TC_HARVEST_SEED_UNIQUE_CODE_PREFIX)) {
            return null;
        }
        String idStr = uniqueCode.substring(TC_HARVEST_SEED_UNIQUE_CODE_PREFIX.length());
        if (!idStr.matches("\\d+")) {
            return null;
        }
        return tcHarvestSeedTbMapper.selectById(Integer.valueOf(idStr));
    }
}
