package com.bio.drqi.manage.flowtask.seed;


import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.SeedStockOutLog;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.mapper.SeedStockOutLogMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.flow.hander.DefaultBuildHtmlModelHandler;
import com.bio.flow.service.BaseTaskService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
public abstract class AbstractSeedTaskService extends DefaultBuildHtmlModelHandler implements BaseTaskService {

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;


    protected void checkSeedStock(String seedNum, BigDecimal num) {
        log.info("出库校验库存：seedNum={}，num={}", seedNum, num);
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum);
        if (seedStockTb == null) {
            throw new BusinessException("不存在此种子号：" + seedNum);
        }
        if (seedStockTb.getSeedNumber().compareTo(num) < 0) {
            log.error("出库校验：{}种子当前库存={},出库库存={}", seedNum, seedStockTb.getSeedNum(), num);
            throw new BusinessException("种子" + seedStockTb.getSeedNum() + "库存不足, 当前库存：" + seedStockTb.getSeedNumber() + seedStockTb.getUnit());
        }

    }

    /**
     * 直接扣减库存
     *
     * @param seedNum
     * @param bioTaskDtlTb
     * @param num
     * @param remarks
     * @param n
     */
    protected ReduceSeedStockResult reduceSeedStock(String seedNum, BioTaskDtlTb bioTaskDtlTb, BigDecimal num, String remarks, int n, String useToDesc) {
        log.info("扣减库存 seedNum={}，num={}", seedNum, num);
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum);
        if (seedStockTb == null) {
            throw new BusinessException("不存在此种子号：" + seedNum);
        }
        if (seedStockTb.getSeedNumber().compareTo(num) < 0) {
            log.error("扣减库存：{}种子当前库存={},出库库存={}", seedNum, seedStockTb.getSeedNum(), num);
            throw new BusinessException("种子" + seedStockTb.getSeedNum() + "库存不足, 当前库存：" + seedStockTb.getSeedNumber() + seedStockTb.getUnit());
        }

        BigDecimal stockBeforeNumber = seedStockTb.getSeedNumber();
        BigDecimal stockAfterNumber = stockBeforeNumber.subtract(num);

        //减库存
        seedStockTb.setSeedNumber(stockAfterNumber);
        seedStockTbMapper.updateById(seedStockTb);

        //记录日志
        SeedStockOutLog seedStockOutLog = new SeedStockOutLog();
        seedStockOutLog.setSeedNum(seedStockTb.getSeedNum());
        seedStockOutLog.setUseToCode(bioTaskDtlTb.getTaskTypeCode());
        seedStockOutLog.setUnit(seedStockTb.getUnit());
        seedStockOutLog.setSeedNumber(num);
        seedStockOutLog.setRemarks(remarks);
        seedStockOutLog.setCreateTime(new Date());
        seedStockOutLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        seedStockOutLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        seedStockOutLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        seedStockOutLog.setOutTaskNum(bioTaskDtlTb.getTaskNum() + n);
        seedStockOutLog.setUseToDesc(useToDesc);
        fillStockSnapshot(seedStockOutLog, seedStockTb, stockBeforeNumber, stockAfterNumber);
        seedStockOutLogMapper.insert(seedStockOutLog);
        return new ReduceSeedStockResult(seedStockTb, stockBeforeNumber, stockAfterNumber);
    }

    private void fillStockSnapshot(SeedStockOutLog seedStockOutLog, SeedStockTb seedStockTb, BigDecimal stockBeforeNumber, BigDecimal stockAfterNumber) {
        seedStockOutLog.setPlantCode(seedStockTb.getPlantCode());
        seedStockOutLog.setParentNum(seedStockTb.getParentNum());
        seedStockOutLog.setFatherInfo(seedStockTb.getFatherInfo());
        seedStockOutLog.setMatherInfo(seedStockTb.getMatherInfo());
        seedStockOutLog.setGeneration(seedStockTb.getGeneration());
        seedStockOutLog.setSpeciesCode(seedStockTb.getSpeciesCode());
        seedStockOutLog.setBreedCode(seedStockTb.getBreedCode());
        seedStockOutLog.setPollinationMethod(seedStockTb.getPollinationMethod());
        seedStockOutLog.setHarvestType(seedStockTb.getHarvestType());
        seedStockOutLog.setHarvestTime(seedStockTb.getHarvestTime());
        seedStockOutLog.setSourceType(seedStockTb.getSourceType());
        seedStockOutLog.setProductionLocationCode(seedStockTb.getProductionLocationCode());
        seedStockOutLog.setStockLocationNum(seedStockTb.getStockLocationNum());
        seedStockOutLog.setTotalNumber(seedStockTb.getTotalNumber());
        seedStockOutLog.setTargetCharacter(seedStockTb.getTargetCharacter());
        seedStockOutLog.setAliasName(seedStockTb.getAliasName());
        seedStockOutLog.setGeneType(seedStockTb.getGeneType());
        seedStockOutLog.setMaterialType(seedStockTb.getMaterialType());
        seedStockOutLog.setMatherSeedNum(seedStockTb.getMatherSeedNum());
        seedStockOutLog.setFatherSeedNum(seedStockTb.getFatherSeedNum());
        seedStockOutLog.setMatherRegionNum(seedStockTb.getMatherRegionNum());
        seedStockOutLog.setFatherRegionNum(seedStockTb.getFatherRegionNum());
        seedStockOutLog.setGenealogy(seedStockTb.getGenealogy());
        seedStockOutLog.setGeneSeparateFlag(seedStockTb.getGeneSeparateFlag());
        seedStockOutLog.setTransFlag(seedStockTb.getTransFlag());
        seedStockOutLog.setVectorTaskCode(seedStockTb.getVectorTaskCode());
        seedStockOutLog.setExperimentNum(seedStockTb.getExperimentNum());
        seedStockOutLog.setProjectCode(seedStockTb.getProjectCode());
        seedStockOutLog.setFatherSingleNum(seedStockTb.getFatherSingleNum());
        seedStockOutLog.setMatherSingleNum(seedStockTb.getMatherSingleNum());
        seedStockOutLog.setPdImplementCode(seedStockTb.getPdImplementCode());
        seedStockOutLog.setStockBeforeNumber(stockBeforeNumber);
        seedStockOutLog.setStockAfterNumber(stockAfterNumber);
    }

    protected static class ReduceSeedStockResult {
        private final SeedStockTb seedStockTb;
        private final BigDecimal stockBeforeNumber;
        private final BigDecimal stockAfterNumber;

        private ReduceSeedStockResult(SeedStockTb seedStockTb, BigDecimal stockBeforeNumber, BigDecimal stockAfterNumber) {
            this.seedStockTb = seedStockTb;
            this.stockBeforeNumber = stockBeforeNumber;
            this.stockAfterNumber = stockAfterNumber;
        }

        protected SeedStockTb getSeedStockTb() {
            return seedStockTb;
        }

        protected BigDecimal getStockBeforeNumber() {
            return stockBeforeNumber;
        }

        protected BigDecimal getStockAfterNumber() {
            return stockAfterNumber;
        }
    }


}
