package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.bsm.dto.BmsProductInputDTO;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.BmsOrderDetailTb;
import com.bio.drqi.domain.BmsOrderTb;
import com.bio.drqi.mapper.BmsOrderDetailTbMapper;
import com.bio.drqi.mapper.BmsOrderTbMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品入库
 */

@Service("bms_product_input")
public class BmsProductInputTaskService extends AbstractBsmBaseTaskService {

    @Resource
    private BmsOrderTbMapper bmsOrderTbMapper;

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        List<BmsProductInputDTO> bmsProductInputDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskNum(), BmsProductInputDTO.class);
        if (CollectionUtil.isEmpty(bmsProductInputDTOList)) {
            throw new BusinessException("请选择关联订单信息");
        }
        for (BmsProductInputDTO bmsProductInputDTO : bmsProductInputDTOList) {
            ValidatorUtil.validator(bmsProductInputDTO);
            BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectOneByOrderDetailNum(bmsProductInputDTO.getOrderDetailNum());
        }

    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
