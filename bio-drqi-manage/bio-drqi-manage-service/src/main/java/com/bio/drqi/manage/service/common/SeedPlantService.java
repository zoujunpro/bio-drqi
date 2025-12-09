package com.bio.drqi.manage.service.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.enums.SeedSourceEnum;
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


    public void seedInStockAddRefPlant(SeedStockTb seedStockTb) {
        //大田校验
        if (SeedSourceEnum.CODE_4.code.equals(seedStockTb.getSourceType())) {
            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber(seedStockTb.getExperimentNum(), seedStockTb.getFatherRegionNum(), seedStockTb.getMatherRegionNum(), seedStockTb.getFatherSeedNum(), seedStockTb.getMatherSeedNum(), seedStockTb.getFatherSingleNum(), seedStockTb.getMatherSingleNum());
            if (tcPollinationTb == null) {
                throw new BusinessException("回关大田种子编号异常，无此授粉信息或者授粉信息不匹配：当前对应数据行的试验方案：" + seedStockTb.getExperimentNum() + " 父本种子编号：" + seedStockTb.getFatherSeedNum() + " 母本种子编号：" + seedStockTb.getMatherSeedNum());
            }
            List<String> seedNumList = JSONUtil.toList(tcPollinationTb.getSeedNums(), String.class);
            if (CollectionUtil.isEmpty(seedNumList)) {
                seedNumList = new ArrayList<>();
            }
            if (!seedNumList.contains(seedStockTb.getSeedNum())) {
                seedNumList.add(seedStockTb.getSeedNum());
            }
            tcPollinationTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            tcPollinationTbMapper.updateById(tcPollinationTb);
        }

    }

    public void seedInStockDeleteRefPlant(SeedStockTb seedStockTb) {
        if (SeedSourceEnum.CODE_4.code.equals(seedStockTb.getSourceType())) {
            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber(seedStockTb.getExperimentNum(), seedStockTb.getFatherRegionNum(), seedStockTb.getMatherRegionNum(), seedStockTb.getFatherSeedNum(), seedStockTb.getMatherSeedNum(), seedStockTb.getFatherSingleNum(), seedStockTb.getMatherSingleNum());
            if (tcPollinationTb == null) {
                throw new BusinessException("回关大田种子编号异常，无此授粉信息或者授粉信息不匹配：当前对应数据行的试验方案：" + seedStockTb.getExperimentNum() + " 父本种子编号：" + seedStockTb.getFatherSeedNum() + " 母本种子编号：" + seedStockTb.getMatherSeedNum());
            }
            List<String> seedNumList = JSONUtil.toList(tcPollinationTb.getSeedNums(), String.class);
            if (CollectionUtil.isEmpty(seedNumList)) {
                seedNumList = new ArrayList<>();
            }
            if (seedNumList.contains(seedStockTb.getSeedNum())) {
                seedNumList.remove(seedStockTb.getSeedNum());
            }
            tcPollinationTb.setSeedNums(JSONUtil.toJsonStr(seedNumList));
            tcPollinationTbMapper.updateById(tcPollinationTb);
        }
    }
}
