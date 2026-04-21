package com.bio.drqi.manage.flowtask.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.SeedStockDestructionLog;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.manage.dto.seed.SeedDestructionDTO;
import com.bio.drqi.mapper.SeedStockDestructionLogMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.flow.dto.BioHtmlModelDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
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
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        SeedDestructionDTO seedDestructionDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);
        for (int i = 0; i < seedDestructionDTO.getSeedList().size(); i++) {
            SeedDestructionDTO.SeedDTO seedDTO = seedDestructionDTO.getSeedList().get(i);
            ValidatorUtil.validator(seedDestructionDTO);
            checkSeedStock(seedDTO.getSeedNum(), seedDTO.getSeedNumber());
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {

            SeedDestructionDTO seedDestructionDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);

            if (CollectionUtil.isEmpty(seedDestructionDTO.getDestructionEvidenceList())) {
                throw new BusinessException("销毁证据缺失");
            }
            if (StringUtils.isEmpty(seedDestructionDTO.getDestructionMethod())) {
                throw new BusinessException("销毁方式缺失");
            }
            if (StringUtils.isEmpty(seedDestructionDTO.getDestructionLocation())) {
                throw new BusinessException("销毁地点缺失");
            }


            for (int i = 0; i < seedDestructionDTO.getSeedList().size(); i++) {
                SeedDestructionDTO.SeedDTO seedDTO = seedDestructionDTO.getSeedList().get(i);
                //扣减冻结库存，记录出库日志
                reduceSeedStock(seedDTO.getSeedNum(), bioTaskDtlTb, seedDTO.getSeedNumber(), seedDTO.getRemarks(), i + 1, USE_TO_DESC);
                //记录销毁信息
                writeSeedDestructionLog(bioTaskDtlTb, seedDTO, seedDestructionDTO);
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }


    private void writeSeedDestructionLog(BioTaskDtlTb bioTaskDtlTb, SeedDestructionDTO.SeedDTO seedDTO, SeedDestructionDTO seedDestructionDTO) {
        SeedStockDestructionLog seedStockDestructionLog = new SeedStockDestructionLog();
        seedStockDestructionLog.setSeedNum(seedDTO.getSeedNum());
        seedStockDestructionLog.setUnit(seedDTO.getUnit());
        seedStockDestructionLog.setSeedNumber(seedDTO.getSeedNumber());
        seedStockDestructionLog.setRemarks(seedDTO.getRemarks());
        seedStockDestructionLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        seedStockDestructionLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        seedStockDestructionLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        seedStockDestructionLog.setDestructionLocation(seedDestructionDTO.getDestructionLocation());
        seedStockDestructionLog.setDestructionMethod(seedDestructionDTO.getDestructionMethod());
        seedStockDestructionLog.setDestructionEvidence(JSONUtil.toJsonStr(seedDestructionDTO.getDestructionEvidenceList()));
        seedStockDestructionLog.setDestructionTime(new Date());
        seedStockDestructionLogMapper.insert(seedStockDestructionLog);
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        return Collections.emptyList();
    }
}
