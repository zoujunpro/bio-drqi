package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.dto.BmsProductInputDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

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
            if (CollectionUtil.isNotEmpty(bmsProductInputDTO.getStockLocationNumberList())) {
                bmsProductInputDTO.getStockLocationNumberList().forEach(stockLocationNumber -> {
                    BmsStockLocationDict bmsStockLocationDict = bmsStockLocationDictMapper.selectOneByUnitCodeAndLocaltionNumber(bmsOrderDetailTb.getApplyUnitCode(), stockLocationNumber);
                    if (bmsStockLocationDict == null) {
                        throw new BusinessException("库存信息不存在");
                    }
                });

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
                // 入库库存
                BmsProductStockTb bmsProductStockTb = updateOrInsertBmsProductStock(bmsProductInputDTO, bmsOrderDetailTb, batchNo);
                //记录入库记录
                insertBmsProductStockInLog(bioTaskDtlTb, bmsProductInputDTO, bmsOrderDetailTb);

                //订单明细接收数量增加
                bmsOrderDetailTb.setReceiveNumber(bmsOrderDetailTb.getReceiveNumber() + bmsProductInputDTO.getNumber());
                bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);

                //判断订单是否已经结束，如果已经结束则更新状态;
                List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectAllByOrderNum(bmsOrderDetailTb.getOrderNum());
                if (bmsOrderDetailTbList.stream().filter(orderDetail -> orderDetail.getPurchaseNumber().intValue() != orderDetail.getReceiveNumber().intValue()).count() == 0) {
                    BmsOrderTb bmsOrderTb = bmsOrderTbMapper.selectOneByOrderNum(bmsOrderDetailTb.getOrderNum());
                    bmsOrderTb.setOverFlag(BioBsmContents.Y);
                    bmsOrderTbMapper.updateById(bmsOrderTb);
                }
            }

        }

    }


    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }


    private BmsProductStockTb updateOrInsertBmsProductStock(BmsProductInputDTO bmsProductInputDTO, BmsOrderDetailTb bmsOrderDetailTb, String batchNo) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByBrandCodeAndProductSpecsAndProductNameAndBatchNoAndUnitCode(bmsOrderDetailTb.getBrandCode(), bmsOrderDetailTb.getProductSpecs(), bmsOrderDetailTb.getProductName(), batchNo, bmsOrderDetailTb.getApplyUnitCode());
        if (bmsProductStockTb == null) {
            bmsProductStockTb = new BmsProductStockTb();
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
            bmsProductStockTb.setUnitCode(bmsOrderDetailTb.getApplyUnitCode());
            bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(bmsProductInputDTO.getStockLocationNumberList()));
            bmsProductStockTbMapper.insert(bmsProductStockTb);
        } else {
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber() + bmsProductInputDTO.getNumber());
            bmsProductStockTb.setTotalStoreNumber(bmsProductStockTb.getTotalStoreNumber() + bmsProductInputDTO.getNumber());
            if (CollectionUtil.isNotEmpty(bmsProductInputDTO.getStockLocationNumberList())) {
                List<String> currentStockLocationNumberList = JSONUtil.toList(bmsProductStockTb.getStockLocationNumber(), String.class);
                if (CollectionUtil.isEmpty(currentStockLocationNumberList)) {
                    currentStockLocationNumberList = new ArrayList<>();
                }
                currentStockLocationNumberList.addAll(bmsProductInputDTO.getStockLocationNumberList());
                bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(currentStockLocationNumberList));
            }
            bmsProductStockTbMapper.updateById(bmsProductStockTb);
        }

        return bmsProductStockTb;
    }

    private void insertBmsProductStockInLog(BioTaskDtlTb bioTaskDtlTb, BmsProductInputDTO bmsProductInputDTO, BmsOrderDetailTb bmsOrderDetailTb) {
        BmsProductStockInLog bmsProductStockInLog = new BmsProductStockInLog();
        bmsProductStockInLog.setOrderDetailNum(bmsOrderDetailTb.getOrderDetailNum());
        bmsProductStockInLog.setProductName(bmsOrderDetailTb.getProductName());
        bmsProductStockInLog.setProductOutCode(bmsOrderDetailTb.getProductOutCode());
        bmsProductStockInLog.setProductCategoryCode(bmsOrderDetailTb.getProductCategoryCode());
        bmsProductStockInLog.setProductCategoryName(bmsOrderDetailTb.getProductCategoryName());
        bmsProductStockInLog.setBrandCode(bmsOrderDetailTb.getBrandCode());
        bmsProductStockInLog.setBrandName(bmsOrderDetailTb.getBrandName());
        bmsProductStockInLog.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
        bmsProductStockInLog.setBatchNo(bmsProductInputDTO.getBatchNo());
        bmsProductStockInLog.setProjectCode(bmsOrderDetailTb.getProjectCode());
        bmsProductStockInLog.setProductPrice(bmsOrderDetailTb.getPurchasePrice());
        bmsProductStockInLog.setStoreNumber(bmsProductInputDTO.getNumber());
        bmsProductStockInLog.setStoreAmount(bmsOrderDetailTb.getPurchasePrice().multiply(new BigDecimal(bmsProductInputDTO.getNumber())));
        bmsProductStockInLog.setApplyUserId(SecurityContextHolder.getUserId());
        bmsProductStockInLog.setApplyUserName(SecurityContextHolder.getNickName());
        bmsProductStockInLog.setCreateTime(new Date());
        bmsProductStockInLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        bmsProductStockInLog.setOrderNum(bmsOrderDetailTb.getOrderNum());
        bmsProductStockInLog.setStockLocationNumber(JSONUtil.toJsonStr(bmsProductInputDTO.getStockLocationNumberList()));
        bmsProductStockInLog.setUnitCode(bmsOrderDetailTb.getApplyUnitCode());
        bmsProductStockInLogMapper.insert(bmsProductStockInLog);
    }
}
