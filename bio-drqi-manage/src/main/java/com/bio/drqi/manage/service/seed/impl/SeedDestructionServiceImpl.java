package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.seed.SeedDestructionPageReqDTO;
import com.bio.drqi.seed.SeedDestructionPageRspDTO;
import com.bio.drqi.domain.SeedStockDestructionLog;
import com.bio.drqi.manage.service.seed.SeedDestructionService;
import com.bio.drqi.mapper.SeedStockDestructionLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeedDestructionServiceImpl implements SeedDestructionService {

    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;


    @Override
    public PageInfo<SeedDestructionPageRspDTO> listPage(SeedDestructionPageReqDTO seedDestructionPageReqDTO) {
        PageInfo<SeedDestructionPageRspDTO> pageInfo = new PageInfo<SeedDestructionPageRspDTO>();
        PageHelper.startPage(seedDestructionPageReqDTO.getPageNum(), seedDestructionPageReqDTO.getPageSize());
        List<SeedStockDestructionLog> seedStockDestructionLogList = seedStockDestructionLogMapper.selectSelective(null);
        PageInfo<SeedStockDestructionLog> srcPageInfo = new PageInfo<>(seedStockDestructionLogList);
        List<SeedDestructionPageRspDTO> seedDestructionPageRspDTOList = new ArrayList<>();
        for (SeedStockDestructionLog seedStockDestructionLog : seedStockDestructionLogList) {
            SeedDestructionPageRspDTO seedDestructionPageRspDTO = new SeedDestructionPageRspDTO();
            seedDestructionPageRspDTO.setId(seedStockDestructionLog.getId());
            seedDestructionPageRspDTO.setDestructionLocation(seedStockDestructionLog.getDestructionLocation());
            seedDestructionPageRspDTO.setSeedNum(seedStockDestructionLog.getSeedNum());
            seedDestructionPageRspDTO.setDestructionMethod(seedStockDestructionLog.getDestructionMethod());
            seedDestructionPageRspDTO.setUnit(seedStockDestructionLog.getUnit());
            seedDestructionPageRspDTO.setSeedNumber(seedStockDestructionLog.getSeedNumber());
            seedDestructionPageRspDTO.setRemarks(seedStockDestructionLog.getRemarks());
            seedDestructionPageRspDTO.setApplyTaskNum(seedStockDestructionLog.getTaskNum());
            seedDestructionPageRspDTO.setApplyUserId(seedStockDestructionLog.getApplyUserId());
            seedDestructionPageRspDTO.setApplyUserName(seedStockDestructionLog.getApplyUserName());
            seedDestructionPageRspDTO.setDestructionEvidenceList(JSONUtil.toList(seedStockDestructionLog.getDestructionEvidence(), String.class));
            seedDestructionPageRspDTO.setDestructionDate(seedStockDestructionLog.getDestructionTime());
            seedDestructionPageRspDTOList.add(seedDestructionPageRspDTO);
        }
        pageInfo.setList(seedDestructionPageRspDTOList);
        pageInfo.setTotal(srcPageInfo.getTotal());
        return pageInfo;
    }


}
