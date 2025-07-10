package com.bio.drqi.bsm.service.impl;

import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.req.BmsProductStockInLogListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockInLogReturnStockReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogQueryByTaskNumRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockInService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class BmsProductStockInServiceImpl implements BmsProductStockInService {

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Resource
    private BmsOrderTbMapper bmsOrderTbMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Override
    public PageInfo<BmsProductStockInLogListPageRspDTO> listPage(BmsProductStockInLogListPageReqDTO bmsProductStockInLogListPageReqDTO) {
        PageHelper.startPage(bmsProductStockInLogListPageReqDTO.getPageNum(), bmsProductStockInLogListPageReqDTO.getPageSize());
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockInLogListPageReqDTO, BmsProductStockInLog.class));
        PageInfo<BmsProductStockInLog> srcPageInfo = new PageInfo<>(bmsProductStockInLogList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductStockInLogListPageRspDTO.class);
    }

    @Override
    public BmsProductStockInLogDetailRspDTO detail(Integer id) {
        BmsProductStockInLog bmsProductStockInLog = bmsProductStockInLogMapper.selectById(id);
        return BeanUtils.copyProperties(bmsProductStockInLog, BmsProductStockInLogDetailRspDTO.class);
    }

    @Override
    public List<BmsProductStockInLogQueryByTaskNumRspDTO> queryByTaskNum(String taskNum) {
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectAllByTaskNum(taskNum);
        return BeanUtils.copyListProperties(bmsProductStockInLogList, BmsProductStockInLogQueryByTaskNumRspDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnStock(BmsProductStockInLogReturnStockReqDTO bmsProductStockInLogReturnStockReqDTO) {
        BmsProductStockInLog bmsProductStockInLog = bmsProductStockInLogMapper.selectById(bmsProductStockInLogReturnStockReqDTO.getId());
        if (bmsProductStockInLog == null) {
            throw new BusinessException("不存在此商品");
        }
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectOneByOrderDetailNum(bmsProductStockInLog.getOrderDetailNum());
        if (bmsOrderDetailTb == null) {
            throw new BusinessException("数据异常，找不到此订单明细");
        }

        if (bmsProductStockInLog.getStoreNumber() < bmsProductStockInLogReturnStockReqDTO.getReturnNumber()) {
            throw new BusinessException("退货数量过多，最多可退货：" + bmsProductStockInLog.getStoreNumber());
        }
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByProductInnerCodeAndUnitCodeAndBatchNoAndStockCode(bmsProductStockInLog.getProductInnerCode(), bmsProductStockInLog.getUnitCode(), bmsProductStockInLog.getBatchNo(),bmsProductStockInLog.getStockCode());
        if(bmsProductStockTb==null){
            throw new BusinessException("库存中不存在此耗材");
        }
        if(bmsProductStockTb.getCurrentStockNumber()<bmsProductStockInLogReturnStockReqDTO.getReturnNumber()){
            throw new BusinessException("库存台账异常，库存中此耗材数量不足,当前剩余库存："+bmsProductStockTb.getCurrentStockNumber());
        }
        //更新入库记录退货数量
        if (bmsProductStockInLog.getReturnNumber() == null) {
            bmsProductStockInLog.setReturnNumber(bmsProductStockInLogReturnStockReqDTO.getReturnNumber());
        } else {
            bmsProductStockInLog.setReturnNumber(bmsProductStockInLogReturnStockReqDTO.getReturnNumber() + bmsProductStockInLog.getReturnNumber());
        }
        bmsProductStockInLogMapper.updateById(bmsProductStockInLog);

        //更新订单明细退货数量
        if (bmsOrderDetailTb.getReturnNumber() == null) {
            bmsOrderDetailTb.setReturnNumber(bmsProductStockInLogReturnStockReqDTO.getReturnNumber());
        } else {
            bmsOrderDetailTb.setReturnNumber(bmsProductStockInLogReturnStockReqDTO.getReturnNumber() + bmsOrderDetailTb.getReturnNumber());
        }
        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);

        //判断订单是否已经结束，如果已经结束则更新状态;
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectAllByOrderNum(bmsProductStockInLog.getOrderNum());
        if (bmsOrderDetailTbList.stream().filter(orderDetailTb -> orderDetailTb.getPurchaseNumber().intValue() != orderDetailTb.getReceiveNumber().intValue()).count() == 0) {
            BmsOrderTb bmsOrderTb = bmsOrderTbMapper.selectOneByOrderNum(bmsProductStockInLog.getOrderNum());
            bmsOrderTb.setOverFlag(BioBsmContents.Y);
            bmsOrderTbMapper.updateById(bmsOrderTb);
        }
        //更新库存数量
        bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber()-bmsProductStockInLogReturnStockReqDTO.getReturnNumber());
        if(bmsProductStockTb.getReturnNumber()==null){
            bmsProductStockTb.setReturnNumber(bmsProductStockInLogReturnStockReqDTO.getReturnNumber());
        }else {
            bmsProductStockTb.setReturnNumber(bmsProductStockTb.getReturnNumber()+bmsProductStockInLogReturnStockReqDTO.getReturnNumber());
        }
        bmsProductStockTbMapper.updateById(bmsProductStockTb);



        //记录退货日志
        BmsReturnOrderDetailTb bmsReturnOrderDetailTb = new BmsReturnOrderDetailTb();
        bmsReturnOrderDetailTb.setOrderDetailNum(bmsProductStockInLog.getOrderDetailNum());
        bmsReturnOrderDetailTb.setReturnNumber(bmsProductStockInLogReturnStockReqDTO.getReturnNumber());
        bmsReturnOrderDetailTb.setReturnAmount(new BigDecimal(bmsProductStockInLogReturnStockReqDTO.getReturnNumber()).multiply(bmsProductStockInLog.getProductPrice()));
        bmsReturnOrderDetailTb.setApplyUserId(SecurityContextHolder.getUserId());
        bmsReturnOrderDetailTb.setApplyUserName(SecurityContextHolder.getNickName());
        bmsReturnOrderDetailTb.setProductName(bmsProductStockInLog.getProductName());
        bmsReturnOrderDetailTb.setProductPrice(bmsProductStockInLog.getProductPrice());
        bmsReturnOrderDetailTb.setRemark(bmsProductStockInLogReturnStockReqDTO.getRemark());
        bmsReturnOrderDetailTb.setCreateTime(new Date());
        bmsReturnOrderDetailTb.setOrderNum(bmsProductStockInLog.getOrderNum());
        bmsReturnOrderDetailTb.setProductSpecs(bmsProductStockInLog.getProductSpecs());
        bmsReturnOrderDetailTb.setBrandCode(bmsProductStockInLog.getBrandCode());
        bmsReturnOrderDetailTb.setBrandName(bmsProductStockInLog.getBrandName());
        bmsReturnOrderDetailTb.setBatchNo(bmsProductStockInLog.getBatchNo());
        bmsReturnOrderDetailTb.setUnitCode(bmsProductStockInLog.getUnitCode());
        bmsReturnOrderDetailTb.setSupplierName(bmsProductStockInLog.getSupplierName());
        bmsReturnOrderDetailTb.setSupplierCode(bmsProductStockInLog.getSupplierCode());
        bmsReturnOrderDetailTb.setProductInnerCode(bmsProductStockInLog.getProductInnerCode());
        bmsReturnOrderDetailTb.setExpirationDate(bmsProductStockInLog.getExpirationDate());
        bmsReturnOrderDetailTb.setProduceDate(bmsProductStockInLog.getProduceDate());
        bmsReturnOrderDetailTb.setProductOutCode(bmsProductStockInLog.getProductOutCode());
        bmsReturnOrderDetailTb.setTaxRate(bmsProductStockInLog.getTaxRate());
        bmsReturnOrderDetailTb.setInStockId(bmsProductStockInLog.getId());
        bmsReturnOrderDetailTb.setStockCode(bmsProductStockInLog.getStockCode());
        bmsReturnOrderDetailTbMapper.insert(bmsReturnOrderDetailTb);


    }
}
