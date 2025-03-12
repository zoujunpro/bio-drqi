package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.bsm.dto.BmsPurchaseOrderDTO;
import com.bio.drqi.domain.BioTaskDtlTb;
import org.springframework.stereotype.Service;

@Service
public class BmsPurchaseOrderTaskService extends AbstractBsmBaseTaskService {

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        BmsPurchaseOrderDTO bmsPurchaseOrderDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsPurchaseOrderDTO.class);


    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}