package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.bsm.dto.BmsCountPeriodTaskDTO;
import com.bio.drqi.bsm.service.BmsCountPeriodTaskService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class BmsCountPeriodTaskServiceImpl implements BmsCountPeriodTaskService {

    @Resource
    private BmsProductStockPeriodCountTbMapper bmsProductStockPeriodCountTbMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;


    @Override
    public void createPeriodData(String dateTime, List<BmsCountPeriodTaskDTO> list) {
        for (BmsCountPeriodTaskDTO bmsCountPeriodTaskDTO : list) {
            log.info("处理库存数据bmsCountPeriodTaskDTO=" + JSONUtil.toJsonStr(bmsCountPeriodTaskDTO));
            DateTime dateMonth = DateUtil.parse(dateTime, DatePattern.NORM_MONTH_PATTERN);
            String lastMonth = DateUtil.format(DateUtil.offsetMonth(dateMonth, -1), DatePattern.NORM_MONTH_PATTERN);
            BmsProductStockPeriodCountTb lastBmsProductStockPeriodCountTb = bmsProductStockPeriodCountTbMapper.selectOneByProductInnerCodeAndUnitCodeAndStockCodeAndBatchNoAndPeriodTime(bmsCountPeriodTaskDTO.getProductInnerCode(), bmsCountPeriodTaskDTO.getUnitCode(), bmsCountPeriodTaskDTO.getStockCode(), bmsCountPeriodTaskDTO.getBatchNo(), lastMonth);
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByUniqueCode(bmsCountPeriodTaskDTO.getUniqueCode());
            //本期第一次采购
            BmsProductStockPeriodCountTb bmsProductStockPeriodCountTb = new BmsProductStockPeriodCountTb();
            bmsProductStockPeriodCountTb.setProductName(bmsProductStockTb.getProductName());
            bmsProductStockPeriodCountTb.setProductOutCode(bmsProductStockTb.getProductOutCode());
            bmsProductStockPeriodCountTb.setProductCategoryCode(bmsProductStockTb.getProductCategoryCode());
            bmsProductStockPeriodCountTb.setBrandCode(bmsProductStockTb.getBrandCode());
            bmsProductStockPeriodCountTb.setProductSpecs(bmsProductStockTb.getProductSpecs());
            bmsProductStockPeriodCountTb.setBatchNo(bmsCountPeriodTaskDTO.getBatchNo());
            bmsProductStockPeriodCountTb.setUnitCode(bmsCountPeriodTaskDTO.getUnitCode());
            bmsProductStockPeriodCountTb.setSupplierCode(bmsProductStockTb.getSupplierCode());
            bmsProductStockPeriodCountTb.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
            bmsProductStockPeriodCountTb.setUniqueCode(bmsProductStockTb.getUniqueCode());
            bmsProductStockPeriodCountTb.setStockCode(bmsProductStockTb.getStockCode());
            bmsProductStockPeriodCountTb.setPeriodBeginNumber(lastBmsProductStockPeriodCountTb!=null?lastBmsProductStockPeriodCountTb.getPeriodEndNumber():0);
            bmsProductStockPeriodCountTb.setPeriodEndNumber(bmsCountPeriodTaskDTO.getCurrentStockNumber());
            bmsProductStockPeriodCountTb.setTotalInNumber(0);
            bmsProductStockPeriodCountTb.setTotalOutNumber(0);
            bmsProductStockPeriodCountTb.setPeriodTime(dateTime);
            bmsProductStockPeriodCountTb.setReturnNumber(0);
            bmsProductStockPeriodCountTb.setMoveInNumber(0);
            bmsProductStockPeriodCountTb.setMoveOutNumber(0);
            bmsProductStockPeriodCountTbMapper.insert(bmsProductStockPeriodCountTb);
        }
        //处理入库
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectAllByCreateTime(dateTime);
        if (CollectionUtil.isNotEmpty(bmsProductStockInLogList)) {
            for (BmsProductStockInLog bmsProductStockInLog : bmsProductStockInLogList) {
                log.info("处理入库数据bmsCountPeriodTaskDTO=" + JSONUtil.toJsonStr(bmsProductStockInLog));
                BmsProductStockPeriodCountTb bmsProductStockPeriodCountTb = bmsProductStockPeriodCountTbMapper.selectOneByProductInnerCodeAndUnitCodeAndStockCodeAndBatchNoAndPeriodTime(bmsProductStockInLog.getProductInnerCode(), bmsProductStockInLog.getUnitCode(), bmsProductStockInLog.getStockCode(), bmsProductStockInLog.getBatchNo(), dateTime);
                bmsProductStockPeriodCountTb.setTotalInNumber(bmsProductStockPeriodCountTb.getTotalInNumber() + bmsProductStockInLog.getStoreNumber());
                bmsProductStockPeriodCountTbMapper.updateById(bmsProductStockPeriodCountTb);
            }
        }

        //处理调拨数据
        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectAllByCreateTime(dateTime);
        if (CollectionUtil.isNotEmpty(bmsMoveOrderDetailTbList)) {
            bmsMoveOrderDetailTbList.stream().forEach(bmsMoveOrderDetailTb -> {
                log.info("处理调拨数据bmsMoveOrderDetailTb=" + JSONUtil.toJsonStr(bmsMoveOrderDetailTb));
                BmsProductStockPeriodCountTb fromBmsProductStockPeriodCountTb = bmsProductStockPeriodCountTbMapper.selectOneByProductInnerCodeAndUnitCodeAndStockCodeAndBatchNoAndPeriodTime(bmsMoveOrderDetailTb.getProductInnerCode(), bmsMoveOrderDetailTb.getUnitCode(), bmsMoveOrderDetailTb.getFromStockCode(), bmsMoveOrderDetailTb.getBatchNo(), dateTime);
                BmsProductStockPeriodCountTb toBmsProductStockPeriodCountTb = bmsProductStockPeriodCountTbMapper.selectOneByProductInnerCodeAndUnitCodeAndStockCodeAndBatchNoAndPeriodTime(bmsMoveOrderDetailTb.getProductInnerCode(), bmsMoveOrderDetailTb.getUnitCode(), bmsMoveOrderDetailTb.getToStockCode(), bmsMoveOrderDetailTb.getBatchNo(), dateTime);
                fromBmsProductStockPeriodCountTb.setMoveOutNumber(fromBmsProductStockPeriodCountTb.getMoveOutNumber() + bmsMoveOrderDetailTb.getMoveNumber());
                toBmsProductStockPeriodCountTb.setMoveInNumber(fromBmsProductStockPeriodCountTb.getMoveInNumber() + bmsMoveOrderDetailTb.getMoveNumber());
                bmsProductStockPeriodCountTbMapper.updateById(fromBmsProductStockPeriodCountTb);
                bmsProductStockPeriodCountTbMapper.updateById(toBmsProductStockPeriodCountTb);
            });
        }
        //处理出库
        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectAllByCreateTime(dateTime);
        if (CollectionUtil.isNotEmpty(bmsProductStockOutLogList)) {
            bmsProductStockOutLogList.forEach(bmsProductStockOutLog -> {
                log.info("处理出库数据bmsProductStockOutLog=" + JSONUtil.toJsonStr(bmsProductStockOutLog));
                BmsProductStockPeriodCountTb bmsProductStockPeriodCountTb = bmsProductStockPeriodCountTbMapper.selectOneByProductInnerCodeAndUnitCodeAndStockCodeAndBatchNoAndPeriodTime(bmsProductStockOutLog.getProductInnerCode(), bmsProductStockOutLog.getUnitCode(), bmsProductStockOutLog.getStockCode(), bmsProductStockOutLog.getBatchNo(), dateTime);
                bmsProductStockPeriodCountTb.setTotalOutNumber(bmsProductStockOutLog.getOutNumber() + bmsProductStockPeriodCountTb.getTotalOutNumber());
                bmsProductStockPeriodCountTbMapper.updateById(bmsProductStockPeriodCountTb);
            });
        }
        //处理退货
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectAllByCreateTime(dateTime);
        if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList)) {
            bmsReturnOrderDetailTbList.forEach(bmsReturnOrderDetailTb -> {
                log.info("处理退货数据bmsReturnOrderDetailTb=" + JSONUtil.toJsonStr(bmsReturnOrderDetailTb));
                BmsProductStockPeriodCountTb bmsProductStockPeriodCountTb = bmsProductStockPeriodCountTbMapper.selectOneByProductInnerCodeAndUnitCodeAndStockCodeAndBatchNoAndPeriodTime(bmsReturnOrderDetailTb.getProductInnerCode(), bmsReturnOrderDetailTb.getUnitCode(), bmsReturnOrderDetailTb.getStockCode(), bmsReturnOrderDetailTb.getBatchNo(), dateTime);
                bmsProductStockPeriodCountTb.setReturnNumber(bmsProductStockPeriodCountTb.getReturnNumber() + bmsReturnOrderDetailTb.getReturnNumber());
                bmsProductStockPeriodCountTbMapper.updateById(bmsProductStockPeriodCountTb);
            });
        }


    }

    public static void main(String[] args) {
        DateTime dateMonth = DateUtil.parse("2025-01", DatePattern.NORM_MONTH_PATTERN);
        String lastMonth = DateUtil.format(DateUtil.offsetMonth(dateMonth, -1), DatePattern.NORM_MONTH_PATTERN);
        System.out.println(lastMonth);
    }
}
