package com.bio.drqi.manage.service.seed.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.manage.seed.SeedStockPageReqDTO;

import java.math.BigDecimal;

public final class SeedStockQueryBuilder {

    private SeedStockQueryBuilder() {
    }

    public static LambdaQueryWrapper<SeedStockTb> build(SeedStockPageReqDTO req, Integer submitUserId,
                                                         boolean requireStock) {
        LambdaQueryWrapper<SeedStockTb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(req.getId() != null, SeedStockTb::getId, req.getId())
                .like(StringUtils.isNotEmpty(req.getPlantCode()), SeedStockTb::getPlantCode, req.getPlantCode())
                .eq(StringUtils.isNotEmpty(req.getSeedNum()), SeedStockTb::getSeedNum, req.getSeedNum())
                .eq(StringUtils.isNotEmpty(req.getVectorTaskCode()), SeedStockTb::getVectorTaskCode, req.getVectorTaskCode())
                .eq(StringUtils.isNotEmpty(req.getProjectCode()), SeedStockTb::getProjectCode, req.getProjectCode())
                .eq(StringUtils.isNotEmpty(req.getParentNum()), SeedStockTb::getPlantCode, req.getParentNum())
                .eq(StringUtils.isNotEmpty(req.getGeneration()), SeedStockTb::getGeneration, req.getGeneration())
                .eq(StringUtils.isNotEmpty(req.getSpecies()), SeedStockTb::getSpeciesCode, req.getSpecies())
                .eq(StringUtils.isNotEmpty(req.getBreedCode()), SeedStockTb::getBreedCode, req.getBreedCode())
                .eq(StringUtils.isNotEmpty(req.getPollinationMethod()), SeedStockTb::getPollinationMethod, req.getPollinationMethod())
                .eq(StringUtils.isNotEmpty(req.getHarvestType()), SeedStockTb::getHarvestType, req.getHarvestType())
                .eq(StringUtils.isNotEmpty(req.getSourceType()), SeedStockTb::getSourceType, req.getSourceType())
                .eq(StringUtils.isNotEmpty(req.getProductionLocationCode()), SeedStockTb::getProductionLocationCode, req.getProductionLocationCode())
                .like(StringUtils.isNotEmpty(req.getStockLocationNum()), SeedStockTb::getStockLocationNum, req.getStockLocationNum())
                .eq(submitUserId != null, SeedStockTb::getSubmitUserId, submitUserId)
                .like(StringUtils.isNotEmpty(req.getRemarks()), SeedStockTb::getRemarks, req.getRemarks())
                .like(StringUtils.isNotEmpty(req.getTargetCharacter()), SeedStockTb::getTargetCharacter, req.getTargetCharacter())
                .like(StringUtils.isNotEmpty(req.getAliasName()), SeedStockTb::getAliasName, req.getAliasName())
                .like(StringUtils.isNotEmpty(req.getGeneType()), SeedStockTb::getGeneType, req.getGeneType())
                .eq(StringUtils.isNotEmpty(req.getMaterialType()), SeedStockTb::getMaterialType, req.getMaterialType())
                .eq(StringUtils.isNotEmpty(req.getMatherSeedNum()), SeedStockTb::getMatherSeedNum, req.getMatherSeedNum())
                .eq(StringUtils.isNotEmpty(req.getMatherSingleNum()), SeedStockTb::getMatherSingleNum, req.getMatherSingleNum())
                .eq(StringUtils.isNotEmpty(req.getPdImplementCode()), SeedStockTb::getPdImplementCode, req.getPdImplementCode())
                .ge(StringUtils.isNotEmpty(req.getBeninHarvestTime()), SeedStockTb::getHarvestTime, req.getBeninHarvestTime())
                .le(StringUtils.isNotEmpty(req.getEndHarvestTime()), SeedStockTb::getHarvestTime, req.getEndHarvestTime())
                .gt(requireStock || "Y".equals(req.getFilterNullFlag()), SeedStockTb::getSeedNumber, BigDecimal.ZERO);

        applyCreateTimeRange(wrapper, req);
        applyOrder(wrapper, req.getOrder());
        return wrapper;
    }

    private static void applyCreateTimeRange(LambdaQueryWrapper<SeedStockTb> wrapper, SeedStockPageReqDTO req) {
        if (StringUtils.isNotEmpty(req.getBeginDate())) {
            wrapper.apply("date_format(create_time,'%Y%m%d') >= {0}", req.getBeginDate().replace("-", ""));
        }
        if (StringUtils.isNotEmpty(req.getEndDate())) {
            wrapper.apply("date_format(create_time,'%Y%m%d') <= {0}", req.getEndDate().replace("-", ""));
        }
    }

    private static void applyOrder(LambdaQueryWrapper<SeedStockTb> wrapper, SeedStockPageReqDTO.Order order) {
        if (order == null || StringUtils.isEmpty(order.getFieldName())) {
            wrapper.orderByDesc(SeedStockTb::getId);
            return;
        }

        boolean ascending = "asc".equals(order.getOrderType());
        if ("seedNumber".equals(order.getFieldName())) {
            wrapper.orderBy(true, ascending, SeedStockTb::getSeedNumber);
        } else if ("id".equals(order.getFieldName())) {
            wrapper.orderBy(true, ascending, SeedStockTb::getId);
        } else {
            wrapper.orderByDesc(SeedStockTb::getId);
        }
    }
}
