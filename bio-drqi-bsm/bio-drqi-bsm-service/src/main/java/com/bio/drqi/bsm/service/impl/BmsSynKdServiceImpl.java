package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.enums.BmsKdSynStatusEnum;
import com.bio.drqi.bsm.kd.KdTaskExecuteService;
import com.bio.drqi.bsm.req.BmsSynKdListPageReqDTO;
import com.bio.drqi.bsm.req.BmsSynKdExecuteReqDTO;
import com.bio.drqi.bsm.rsp.BmsSynKdListPageRspDTO;
import com.bio.drqi.bsm.service.BmsSynKdService;
import com.bio.drqi.domain.BmsSynKdTaskLog;
import com.bio.drqi.mapper.BmsSynKdTaskLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BmsSynKdServiceImpl implements BmsSynKdService {

    @Resource
    private KdTaskExecuteService kdTaskExecuteService;

    @Resource
    private BmsSynKdTaskLogMapper bmsSynKdTaskLogMapper;

    @Override
    public PageInfo<BmsSynKdListPageRspDTO> listPage(BmsSynKdListPageReqDTO bmsSynKdListPageReqDTO) {
        PageHelper.startPage(bmsSynKdListPageReqDTO.getPageNum(), bmsSynKdListPageReqDTO.getPageSize());
        List<BmsSynKdTaskLog> bmsSynKdTaskLogList = bmsSynKdTaskLogMapper.selectSelective(BeanUtils.copyProperties(bmsSynKdListPageReqDTO, BmsSynKdTaskLog.class));
        PageInfo<BmsSynKdTaskLog> srcPageInfo = new PageInfo<>(bmsSynKdTaskLogList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsSynKdListPageRspDTO.class);
    }

    @Override
    public void execute(BmsSynKdExecuteReqDTO bmsSynKdExecuteReqDTO) {
        if (StringUtils.isEmpty(bmsSynKdExecuteReqDTO.getBeginDate())) {
            bmsSynKdExecuteReqDTO.setBeginDate("2025-07-01");
        }
        if (StringUtils.isNotEmpty(bmsSynKdExecuteReqDTO.getBeginDate())) {
            Date currentBeignDate = DateUtil.parse(bmsSynKdExecuteReqDTO.getBeginDate(), "yyyy-MM-dd");
            Date minBeignDate = DateUtil.parse("2025-07-01", "yyyy-MM-dd");
            if(currentBeignDate.compareTo(minBeignDate)<0){
                throw new BusinessException("只能同步2025-07-01之后数据");
            }
        }
        List<BmsSynKdTaskLog> list = bmsSynKdTaskLogMapper.selectAllBySynStatusOrderByIdDesc(BmsKdSynStatusEnum.syn.name());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BusinessException("已经有在进行中的任务，请等待执行完毕后再进行金蝶数据同步");
        }

        BmsSynKdTaskLog bmsSynKdTaskLog = new BmsSynKdTaskLog();
        bmsSynKdTaskLog.setCreateTime(new Date());
        bmsSynKdTaskLog.setCreateUserId(SecurityContextHolder.getUserId());
        bmsSynKdTaskLog.setCreateUserName(SecurityContextHolder.getNickName());
        bmsSynKdTaskLog.setSynStatus(BmsKdSynStatusEnum.syn.name());
        bmsSynKdTaskLog.setFailReason(null);
        bmsSynKdTaskLog.setBeginDate(bmsSynKdExecuteReqDTO.getBeginDate());
        bmsSynKdTaskLog.setEndDate(bmsSynKdExecuteReqDTO.getEndDate());
        bmsSynKdTaskLogMapper.insert(bmsSynKdTaskLog);
        new Thread(() -> {
            kdTaskExecuteService.executeSynKd(bmsSynKdTaskLog);
        }).start();

    }

    @Override
    public String findLastSuccessTime() {
        List<BmsSynKdTaskLog> list = bmsSynKdTaskLogMapper.selectAllBySynStatusOrderByIdDesc(BmsKdSynStatusEnum.success.name());
        if (CollectionUtil.isNotEmpty(list)) {
            BmsSynKdTaskLog bmsSynKdTaskLog = list.get(0);
            if (bmsSynKdTaskLog.getEndDate() == null) {
                return DateUtil.format(bmsSynKdTaskLog.getCreateTime(), DatePattern.NORM_DATE_PATTERN);
            } else {
                return bmsSynKdTaskLog.getEndDate();
            }
        }
        return null;
    }
}
