package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.bsm.dto.BmsProductInputDTO;
import com.bio.drqi.domain.BioTaskDtlTb;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品入库
 */

@Service("bms_product_input")
public class BmsProductInputTaskService extends AbstractBsmBaseTaskService {
    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        List<BmsProductInputDTO> bmsProductInputDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskNum(), BmsProductInputDTO.class);
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
