package com.bio.drqi.bsm.scheduled;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.dto.BmsCountPeriodTaskDTO;
import com.bio.drqi.bsm.listener.BmsSpotCheckResultTaskListener;
import com.bio.drqi.bsm.service.BmsCountPeriodTaskService;
import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Service
public class BmsTaskScheduledService {


    @Value("${bio.bmsStockPeriodCountScheduledCron: }")
    private String bmsStockPeriodCountScheduledCron;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsCountPeriodTaskService bmsCountPeriodTaskService;

    @Resource
    private BmsSpotCheckResultTaskListener bmsSpotCheckResultTaskListener;

    @Value("${spring.profiles.active}")
    private String active;

    @Scheduled(cron = "0 0 23 L * ?")
    public void bmsStockPeriodCountScheduledCronTask() {
        if (!active.equals("prod")) {
            return;
        }
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        List<BmsCountPeriodTaskDTO> bmsCountPeriodTaskDTOList = BeanUtils.copyListProperties(bmsProductStockTbList, BmsCountPeriodTaskDTO.class);
        bmsCountPeriodTaskService.createPeriodData(StringUtils.isEmpty(bmsStockPeriodCountScheduledCron) ? DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN) : bmsStockPeriodCountScheduledCron, bmsCountPeriodTaskDTOList);
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void BmsSpotCheckResultScheduledCronTask() {
        if (!active.equals("prod")) {
            return;
        }
        bmsSpotCheckResultTaskListener.notice();
    }
}
