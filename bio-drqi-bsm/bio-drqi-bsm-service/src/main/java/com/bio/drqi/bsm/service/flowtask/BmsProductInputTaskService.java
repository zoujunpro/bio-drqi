package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.bsm.dto.BmsProductInputDTO;
import com.bio.drqi.bsm.dto.BmsPurchaseOrderDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.BmsOrderDetailTbMapper;
import com.bio.drqi.mapper.BmsOrderTbMapper;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
import com.bio.drqi.mapper.BmsStockLocationDictMapper;
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

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsStockLocationDictMapper bmsStockLocationDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        List<BmsProductInputDTO> bmsProductInputDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskNum(), BmsProductInputDTO.class);
        if (CollectionUtil.isEmpty(bmsProductInputDTOList)) {
            throw new BusinessException("请选择关联订单信息");
        }
        for (BmsProductInputDTO bmsProductInputDTO : bmsProductInputDTOList) {
            ValidatorUtil.validator(bmsProductInputDTO);
            BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectOneByOrderDetailNum(bmsProductInputDTO.getOrderDetailNum());
            if (bmsOrderDetailTb.getPurchaseNumber() >= bmsOrderDetailTb.getReceiveNumber()) {
                throw new BusinessException("该耗材已经全部到货");
            }
            if (bmsOrderDetailTb.getPurchaseNumber() - bmsOrderDetailTb.getReceiveNumber() < bmsProductInputDTO.getNumber()) {
                throw new BusinessException("入库数量已经大于剩余待入库数量");
            }
            if(StringUtils.isNotEmpty(bmsProductInputDTO.getStockLocationNumber())){
              BmsStockLocationDict bmsStockLocationDict= bmsStockLocationDictMapper.selectOneByUnitCodeAndLocaltionNumber(bmsOrderDetailTb.getApplyUnitCode(),bmsProductInputDTO.getStockLocationNumber());
              if(bmsStockLocationDict==null){
                  throw new BusinessException("库存信息不存在");
              }
            }
        }

    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            List<BmsProductInputDTO> bmsProductInputDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskNum(), BmsProductInputDTO.class);
            for (BmsProductInputDTO bmsProductInputDTO : bmsProductInputDTOList) {
                BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectOneByOrderDetailNum(bmsProductInputDTO.getOrderDetailNum());
                String batchNo = StringUtils.isEmpty(bmsProductInputDTO.getBatchNo()) ? "N/A" : bmsProductInputDTO.getBatchNo();

                BmsStockLocationDict bmsStockLocationDict= bmsStockLocationDictMapper.selectOneByUnitCodeAndLocaltionNumber(bmsOrderDetailTb.getApplyUnitCode(),bmsProductInputDTO.getStockLocationNumber());


                // 入库
                BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByBrandCodeAndProductSpecsAndProductNameAndBatchNo(bmsOrderDetailTb.getBrandCode(), bmsOrderDetailTb.getProductSpecs(), bmsOrderDetailTb.getProductName(), batchNo);
                if(bmsProductStockTb==null){
                    bmsProductStockTb=new BmsProductStockTb();
                    bmsProductStockTb.setProductName(bmsOrderDetailTb.getProductName());
                    bmsProductStockTb.setProductOutCode(bmsOrderDetailTb.getProductOutCode());
                    bmsProductStockTb.setProductCategoryCode(bmsOrderDetailTb.getProductCategoryCode());
                    bmsProductStockTb.setProductCategoryName(bmsOrderDetailTb.getProductCategoryName());
                    bmsProductStockTb.setBrandCode(bmsOrderDetailTb.getBrandCode());
                    bmsProductStockTb.setBrandName(bmsOrderDetailTb.getBrandName());
                    bmsProductStockTb.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
                    bmsProductStockTb.setBatchNo(batchNo);
                    bmsProductStockTb.setTotalStoreNumber(bmsProductInputDTO.getNumber());
                    bmsProductStockTb.setCurrentStockNumber(bmsProductInputDTO.getNumber());
                    bmsProductStockTb.setTotalOutNumber(bmsProductInputDTO.getNumber());
                    bmsProductStockTb.setProductPrice(bmsOrderDetailTb.getPurchasePrice());
                    bmsProductStockTb.setStockName(bmsStockLocationDict.getStockName());
                    bmsProductStockTb.setStockCode(bmsStockLocationDict.getStockCode());
                    bmsProductStockTb.setStockLocationNumber(bmsProductInputDTO.getStockLocationNumber());
                    bmsProductStockTbMapper.insert(bmsProductStockTb);
                }
                //记录入库记录
                //订单明细接收数量增加
                //判断订单是否已经结束，如果已经结束则更新状态;
            }

        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
