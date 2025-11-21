package com.bio.drqi.manage.flowtask.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.manage.dto.seed.SeedOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 其他出库
 */
@Service("seed_out_apply")
@Slf4j
public class SeedOutApplyProcService extends AbstractSeedTaskService {

    private static final String USE_TO_DESC = "其他";

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
        List<SeedOutDTO.ExecuteFormContent> executeFormContentList = seedOutDTO.getExecuteForm().getExecuteFormContentList();
        Map<String, List<SeedOutDTO.ExecuteFormContent>> map = executeFormContentList.stream().collect(Collectors.groupingBy(SeedOutDTO.ExecuteFormContent::getSeedNum));
        if (CollectionUtil.isNotEmpty(map)) {
            map.forEach((seedNum, executeFormContents) -> {
                List<String> numList = executeFormContents.stream().map(SeedOutDTO.ExecuteFormContent::getNum).collect(Collectors.toList());
                BigDecimal numCount = new BigDecimal("0");
                for (String num:numList){
                    numCount=numCount.add(new BigDecimal(num));
                }
                checkSeedStock(seedNum, numCount);
            });
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        log.info("种子库出库扣减库存开始：taskNum={} status={}", bioTaskDtlTb.getTaskNum(), bioTaskDtlTb.getTaskStatus());
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
            if (Objects.nonNull(seedOutDTO.getExecuteForm()) && seedOutDTO.getExecuteForm().getExecuteFormContentList().size() > 0) {
                for (int i = 0; i < seedOutDTO.getExecuteForm().getExecuteFormContentList().size(); i++) {
                    SeedOutDTO.ExecuteFormContent executeFormContent = seedOutDTO.getExecuteForm().getExecuteFormContentList().get(i);
                    //扣减库存，记录出库日志
                    reduceSeedStock(executeFormContent.getSeedNum(), bioTaskDtlTb, new BigDecimal(executeFormContent.getNum()), executeFormContent.getRemark(), i + 1, seedOutDTO.getApplyFrom().getUseToDesc());
                }
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        //不做任何处理

    }
}
