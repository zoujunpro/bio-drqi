package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.dto.BmsProductInputDTO;
import com.bio.drqi.bsm.dto.BmsProductOutDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private BmsProductOutTaskService bmsProductOutTaskService;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        BmsProductInputDTO bmsProductInputDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsProductInputDTO.class);
        BeanUtils.trimFiledSpace(bmsProductInputDTO);
        if (bmsProductInputDTO == null) {
            throw new BusinessException("入库信息缺失");
        }
        BmsOrderTb bmsOrderTb = bmsOrderTbMapper.selectOneByOrderNum(bmsProductInputDTO.getOrderNum());
        if (bmsOrderTb == null) {
            throw new BusinessException("订单不存在");
        }
        if (BioBsmContents.Y.equals(bmsOrderTb.getOverFlag())) {
            throw new BusinessException("该订单已经完成");
        }
        if(CollectionUtil.isEmpty(bmsProductInputDTO.getOrderDetailList())){
            throw new BusinessException("没有入库数据");
        }

        for (BmsProductInputDTO.OrderDetail orderDetail : bmsProductInputDTO.getOrderDetailList()) {
            ValidatorUtil.validator(bmsProductInputDTO);
            BeanUtils.trimFiledSpace(bmsProductInputDTO);
            BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectOneByOrderDetailNum(orderDetail.getOrderDetailNum());
            if (Objects.equals(bmsOrderDetailTb.getPurchaseNumber(), bmsOrderDetailTb.getReceiveNumber().intValue())) {
                throw new BusinessException("该耗材已经全部到货");
            }
            if (bmsOrderDetailTb.getPurchaseNumber() - bmsOrderDetailTb.getReceiveNumber() < orderDetail.getNumber()) {
                throw new BusinessException("入库数量已经大于剩余待入库数量");
            }
            if (CollectionUtil.isNotEmpty(orderDetail.getStockLocationNumberList())) {
                orderDetail.getStockLocationNumberList().forEach(stockLocationNumber -> {
                    BmsStockLocationDict bmsStockLocationDict = bmsStockLocationDictMapper.selectOneByUnitCodeAndLocationNumber(bmsOrderDetailTb.getApplyUnitCode(), stockLocationNumber);
                    if (bmsStockLocationDict == null) {
                        throw new BusinessException("库存信息不存在");
                    }
                });
            }
            orderDetail.setProjectCode(bmsOrderDetailTb.getProjectCode());
            orderDetail.setProjectName(bmsOrderDetailTb.getProjectName());
            orderDetail.setBrandCode(bmsOrderDetailTb.getBrandCode());
            orderDetail.setBrandName(bmsOrderDetailTb.getBrandName());
            orderDetail.setProductName(bmsOrderDetailTb.getProductName());
            orderDetail.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
            orderDetail.setProductOutCode(bmsOrderDetailTb.getProductOutCode());
            orderDetail.setPurchasePrice(bmsOrderDetailTb.getPurchasePrice());
            orderDetail.setPurchaseNumber(bmsOrderDetailTb.getPurchaseNumber());
            orderDetail.setPayAmount(bmsOrderDetailTb.getPayAmount());
            orderDetail.setProductCategoryCode(bmsOrderDetailTb.getProductCategoryCode());
            orderDetail.setProductCategoryName(bmsOrderDetailTb.getProductCategoryName());
            orderDetail.setApplyUnitCode(bmsOrderDetailTb.getApplyUnitCode());
            orderDetail.setApplyUnitName(bmsOrderDetailTb.getApplyUnitName());
            orderDetail.setProductInnerCode(bmsOrderDetailTb.getProductInnerCode());
            orderDetail.setSupplierCode(bmsOrderDetailTb.getSupplierCode());
        }

        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(bmsProductInputDTO));
        bioTaskDtlTb.setTaskDesc(bmsProductInputDTO.getAllProductName());
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            BmsProductInputDTO bmsProductInputDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsProductInputDTO.class);
            for (BmsProductInputDTO.OrderDetail inputOrderDetail : bmsProductInputDTO.getOrderDetailList()) {
                BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectOneByOrderDetailNum(inputOrderDetail.getOrderDetailNum());
                String batchNo = StringUtils.isEmpty(inputOrderDetail.getBatchNo()) ? "N/A" : inputOrderDetail.getBatchNo();
                // 入库库存updateOrInsertBmsProductStock
                BmsProductStockTb bmsProductStockTb = updateOrInsertBmsProductStock(inputOrderDetail, bmsOrderDetailTb, batchNo);
                //记录入库记录
                insertBmsProductStockInLog(bioTaskDtlTb, inputOrderDetail, bmsOrderDetailTb, bmsProductStockTb);

                //订单明细接收数量增加
                bmsOrderDetailTb.setReceiveNumber(bmsOrderDetailTb.getReceiveNumber() + inputOrderDetail.getNumber());
                bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);

                //入库直接出库
                if (BioBsmContents.Y.equals(bmsProductInputDTO.getOutStockFlag())) {
                    BmsProductOutDTO bmsProductOutDTO = BeanUtils.copyProperties(bmsProductStockTb, BmsProductOutDTO.class);
                    bmsProductOutDTO.setNumber(inputOrderDetail.getNumber());
                    bmsProductOutDTO.setRemark("入库直接出库");
                    bmsProductOutTaskService.doOutStock(bioTaskDtlTb, bmsProductOutDTO);
                }
                //判断订单是否已经结束，如果已经结束则更新状态;
                List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectAllByOrderNum(bmsOrderDetailTb.getOrderNum());
                if (bmsOrderDetailTbList.stream().filter(orderDetailTb -> orderDetailTb.getPurchaseNumber().intValue() != orderDetailTb.getReceiveNumber().intValue()).count() == 0) {
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


    private BmsProductStockTb updateOrInsertBmsProductStock(BmsProductInputDTO.OrderDetail inputOrderDetail, BmsOrderDetailTb bmsOrderDetailTb, String batchNo) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByProductInnerCodeAndUnitCodeAndBatchNo(bmsOrderDetailTb.getProductInnerCode(), bmsOrderDetailTb.getApplyUnitCode(), batchNo);
        if (bmsProductStockTb == null) {
            bmsProductStockTb = new BmsProductStockTb();
            bmsProductStockTb.setProductName(bmsOrderDetailTb.getProductName());
            bmsProductStockTb.setProductOutCode(bmsOrderDetailTb.getProductOutCode());
            bmsProductStockTb.setProductCategoryCode(bmsOrderDetailTb.getProductCategoryCode());
            bmsProductStockTb.setProductTypeCode(bmsOrderDetailTb.getProductTypeCode());
            bmsProductStockTb.setBrandCode(bmsOrderDetailTb.getBrandCode());
            bmsProductStockTb.setBrandName(bmsOrderDetailTb.getBrandName());
            bmsProductStockTb.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
            bmsProductStockTb.setBatchNo(batchNo);
            bmsProductStockTb.setTotalStoreNumber(inputOrderDetail.getNumber());
            bmsProductStockTb.setCurrentStockNumber(inputOrderDetail.getNumber());
            bmsProductStockTb.setTotalOutNumber(0);
            bmsProductStockTb.setUnitCode(bmsOrderDetailTb.getApplyUnitCode());
            bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(inputOrderDetail.getStockLocationNumberList()));
            bmsProductStockTb.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
            bmsProductStockTb.setSupplierName(bmsOrderDetailTb.getSupplierName());
            bmsProductStockTb.setSupplierCode(bmsOrderDetailTb.getSupplierCode());
            bmsProductStockTb.setProductInnerCode(bmsOrderDetailTb.getProductInnerCode());
            bmsProductStockTb.setUniqueCode(IdUtils.simpleUUID());
            bmsProductStockTb.setProduceDate(inputOrderDetail.getProduceDate());
            bmsProductStockTb.setExpirationDate(inputOrderDetail.getExpirationDate());
            bmsProductStockTbMapper.insert(bmsProductStockTb);
        } else {
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber() + inputOrderDetail.getNumber());
            bmsProductStockTb.setTotalStoreNumber(bmsProductStockTb.getTotalStoreNumber() + inputOrderDetail.getNumber());
            if (CollectionUtil.isNotEmpty(inputOrderDetail.getStockLocationNumberList())) {
                List<String> currentStockLocationNumberList = JSONUtil.toList(bmsProductStockTb.getStockLocationNumber(), String.class);
                if (CollectionUtil.isEmpty(currentStockLocationNumberList)) {
                    currentStockLocationNumberList = new ArrayList<>();
                }
                currentStockLocationNumberList.addAll(inputOrderDetail.getStockLocationNumberList());
                bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(currentStockLocationNumberList));
            }
            bmsProductStockTbMapper.updateById(bmsProductStockTb);
        }

        return bmsProductStockTb;
    }

    private void insertBmsProductStockInLog(BioTaskDtlTb bioTaskDtlTb, BmsProductInputDTO.OrderDetail inputOrderDetail, BmsOrderDetailTb bmsOrderDetailTb, BmsProductStockTb bmsProductStockTb) {
        BmsProductStockInLog bmsProductStockInLog = new BmsProductStockInLog();
        bmsProductStockInLog.setOrderDetailNum(bmsOrderDetailTb.getOrderDetailNum());
        bmsProductStockInLog.setProductName(bmsOrderDetailTb.getProductName());
        bmsProductStockInLog.setProductOutCode(bmsOrderDetailTb.getProductOutCode());
        bmsProductStockInLog.setProductCategoryCode(bmsOrderDetailTb.getProductCategoryCode());
        bmsProductStockInLog.setProductTypeCode(bmsOrderDetailTb.getProductTypeCode());
        bmsProductStockInLog.setBrandCode(bmsOrderDetailTb.getBrandCode());
        bmsProductStockInLog.setBrandName(bmsOrderDetailTb.getBrandName());
        bmsProductStockInLog.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
        bmsProductStockInLog.setBatchNo(inputOrderDetail.getBatchNo());
        bmsProductStockInLog.setProjectCode(bmsOrderDetailTb.getProjectCode());
        bmsProductStockInLog.setProductPrice(bmsOrderDetailTb.getPurchasePrice());
        bmsProductStockInLog.setStoreNumber(inputOrderDetail.getNumber());
        bmsProductStockInLog.setStoreAmount(bmsOrderDetailTb.getPurchasePrice().multiply(new BigDecimal(inputOrderDetail.getNumber())));
        bmsProductStockInLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        bmsProductStockInLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        bmsProductStockInLog.setCreateTime(new Date());
        bmsProductStockInLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        bmsProductStockInLog.setOrderNum(bmsOrderDetailTb.getOrderNum());
        bmsProductStockInLog.setStockLocationNumber(JSONUtil.toJsonStr(inputOrderDetail.getStockLocationNumberList()));
        bmsProductStockInLog.setUnitCode(bmsOrderDetailTb.getApplyUnitCode());
        bmsProductStockInLog.setUniqueCode(bmsProductStockTb.getUniqueCode());
        bmsProductStockInLog.setSupplierName(bmsProductStockTb.getSupplierName());
        bmsProductStockInLog.setSupplierCode(bmsProductStockTb.getSupplierCode());
        bmsProductStockInLog.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
        bmsProductStockInLog.setProduceDate(inputOrderDetail.getProduceDate());
        bmsProductStockInLog.setExpirationDate(inputOrderDetail.getExpirationDate());
        bmsProductStockInLog.setTaxRate(bmsOrderDetailTb.getTaxRate());
        bmsProductStockInLogMapper.insert(bmsProductStockInLog);
    }

}
