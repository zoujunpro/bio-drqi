package com.bio.drqi.manage.service.task.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.SeedStockDestructionLog;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.manage.dto.seed.SeedDestructionDTO;
import com.bio.drqi.mapper.SeedStockDestructionLogMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 种子销毁申请
 */
@Service("seed_destruction_apply")
public class SeedDestructionApplyProcService extends AbstractSeedTaskService {

    private static final String USE_TO_DESC = "种子销毁";

    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;


    @Override
    public void taskCheck(BioTaskDtlTb bioTaskDtlTb) {
        List<SeedDestructionDTO> seedDestructionDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);
        for (int i = 0; i < seedDestructionDTOList.size(); i++) {
            SeedDestructionDTO seedDestructionDTO = seedDestructionDTOList.get(i);
            ValidatorUtil.validator(seedDestructionDTO);
            checkSeedStock(seedDestructionDTO.getSeedNum(), seedDestructionDTO.getSeedNumber());
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            List<SeedDestructionDTO> seedDestructionDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);
            for (int i = 0; i < seedDestructionDTOList.size(); i++) {
                SeedDestructionDTO seedDestructionDTO = seedDestructionDTOList.get(i);

                if (CollectionUtil.isEmpty(seedDestructionDTO.getDestructionEvidenceList())) {
                    throw new BusinessException("销毁证据缺失");
                }
                if (StringUtils.isEmpty(seedDestructionDTO.getDestructionMethod())) {
                    throw new BusinessException("销毁方式缺失");
                }
                if (StringUtils.isEmpty(seedDestructionDTO.getDestructionLocation())) {
                    throw new BusinessException("销毁地点缺失");
                }
                //扣减冻结库存，记录出库日志
                reduceSeedStock(seedDestructionDTO.getSeedNum(), bioTaskDtlTb, seedDestructionDTO.getSeedNumber(), seedDestructionDTO.getRemarks(), i + 1, USE_TO_DESC);
                //记录销毁信息
                writeSeedDestructionLog(bioTaskDtlTb, seedDestructionDTO);
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }




    private void writeSeedDestructionLog(BioTaskDtlTb bioTaskDtlTb, SeedDestructionDTO seedDestructionDTO) {
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedDestructionDTO.getSeedNum());
        SeedStockDestructionLog seedStockDestructionLog = new SeedStockDestructionLog();
        seedStockDestructionLog.setSeedNum(seedDestructionDTO.getSeedNum());
        seedStockDestructionLog.setUnit(seedStockTb.getUnit());
        seedStockDestructionLog.setSeedNumber(seedDestructionDTO.getSeedNumber());
        seedStockDestructionLog.setRemarks(seedDestructionDTO.getRemarks());
        seedStockDestructionLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        seedStockDestructionLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        seedStockDestructionLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        seedStockDestructionLog.setDestructionLocation(seedDestructionDTO.getDestructionLocation());
        seedStockDestructionLog.setDestructionMethod(seedDestructionDTO.getDestructionMethod());
        seedStockDestructionLog.setDestructionEvidence(JSONUtil.toJsonStr(seedDestructionDTO.getDestructionEvidenceList()));
        seedStockDestructionLog.setDestructionTime(new Date());
        seedStockDestructionLogMapper.insert(seedStockDestructionLog);
    }
}
