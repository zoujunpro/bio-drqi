package com.bio.drqi.manage.service.task.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.SeedStockDestructionLog;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.SeedDestructionEnum;
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
        SeedDestructionDTO seedDestructionDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);
        if(SeedDestructionEnum.IN.name().equals(seedDestructionDTO.getDestructionType())){
            for (int i = 0; i < seedDestructionDTO.getSeedList().size(); i++) {
                SeedDestructionDTO.SeedDTO seedDTO = seedDestructionDTO.getSeedList().get(i);
                ValidatorUtil.validator(seedDestructionDTO);
                checkSeedStock(seedDTO.getSeedNum(), seedDTO.getSeedNumber());
            }
        }

    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            SeedDestructionDTO seedDestructionDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);
            for (int i = 0; i < seedDestructionDTO.getSeedList().size(); i++) {
                SeedDestructionDTO.SeedDTO seedDTO = seedDestructionDTO.getSeedList().get(i);

                if (CollectionUtil.isEmpty(seedDTO.getDestructionEvidenceList())) {
                    throw new BusinessException("销毁证据缺失");
                }
                if (StringUtils.isEmpty(seedDTO.getDestructionMethod())) {
                    throw new BusinessException("销毁方式缺失");
                }
                if (StringUtils.isEmpty(seedDTO.getDestructionLocation())) {
                    throw new BusinessException("销毁地点缺失");
                }
                if(SeedDestructionEnum.IN.name().equals(seedDestructionDTO.getDestructionType())){
                    //扣减冻结库存，记录出库日志
                    reduceSeedStock(seedDTO.getSeedNum(), bioTaskDtlTb, seedDTO.getSeedNumber(), seedDTO.getRemarks(), i + 1, USE_TO_DESC);
                }
                //记录销毁信息
                writeSeedDestructionLog(bioTaskDtlTb, seedDTO);
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }




    private void writeSeedDestructionLog(BioTaskDtlTb bioTaskDtlTb,  SeedDestructionDTO.SeedDTO seedDTO) {
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedDTO.getSeedNum());
        SeedStockDestructionLog seedStockDestructionLog = new SeedStockDestructionLog();
        seedStockDestructionLog.setSeedNum(seedDTO.getSeedNum());
        seedStockDestructionLog.setUnit(seedStockTb.getUnit());
        seedStockDestructionLog.setSeedNumber(seedDTO.getSeedNumber());
        seedStockDestructionLog.setRemarks(seedDTO.getRemarks());
        seedStockDestructionLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        seedStockDestructionLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        seedStockDestructionLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        seedStockDestructionLog.setDestructionLocation(seedDTO.getDestructionLocation());
        seedStockDestructionLog.setDestructionMethod(seedDTO.getDestructionMethod());
        seedStockDestructionLog.setDestructionEvidence(JSONUtil.toJsonStr(seedDTO.getDestructionEvidenceList()));
        seedStockDestructionLog.setDestructionTime(new Date());
        seedStockDestructionLogMapper.insert(seedStockDestructionLog);
    }
}
