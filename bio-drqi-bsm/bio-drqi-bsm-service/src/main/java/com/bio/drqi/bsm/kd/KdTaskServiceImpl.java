package com.bio.drqi.bsm.kd;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.enums.BmsKdSynStatusEnum;
import com.bio.drqi.bsm.enums.PurchaseUnitEnum;
import com.bio.drqi.bsm.kd.dto.QuerySupplierDTO;
import com.bio.drqi.bsm.kd.enums.OperateEnum;
import com.bio.drqi.bsm.kd.enums.OrgEnum;
import com.bio.drqi.bsm.kd.service.KdApiService;
import com.bio.drqi.bsm.kd.util.KdRequestUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KdTaskServiceImpl implements KdTaskService, KdTaskExecuteService {

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private KdApiService kdApiService;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;

    @Resource
    private BmsSynKdTaskLogMapper bmsSynKdTaskLogMapper;


    public void synProjectTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************项目同步开始**************************");
        List<BmsProjectDict> bmsProjectDictList = bmsProjectDictMapper.selectList(null);
        Map<String, List<BmsProjectDict>> mapList = bmsProjectDictList.stream().collect(Collectors.groupingBy(BmsProjectDict::getKdProjectCode));
        mapList.forEach((kdProjectCode, list) -> {
            if (StringUtils.isNotEmpty(kdProjectCode)) {
                BmsProjectDict bmsProjectDict = list.get(0);
                if (StringUtils.isEmpty(bmsProjectDict.getKdNumber())) {
                    String kdNumber = kdApiService.execute(OperateEnum.projectSave, bmsProjectDict, PurchaseUnitEnum.default_.name());
                    bmsProjectDictMapper.updateKdNumberByKdProjectCode(kdNumber, kdProjectCode);
                } else {
                    kdApiService.execute(OperateEnum.projectModify, bmsProjectDict, PurchaseUnitEnum.default_.name());
                }
            }
        });
        log.info("*****************项目同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);

    }


    @Override
    public void synStockTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************仓库同步开始**************************");
        List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
        for (BmsStockDict bmsStockDict : bmsStockDictList) {
            if (bmsStockDict.getKdNumber() != null && bmsStockDict.getKdNumber() > 0) {
                kdApiService.execute(OperateEnum.stockModify, bmsStockDict, bmsStockDict.getUnitCode());
            } else {
                String kdNumber = kdApiService.execute(OperateEnum.stockSave, bmsStockDict, bmsStockDict.getUnitCode());
                bmsStockDict.setKdNumber(Integer.valueOf(kdNumber));
                bmsStockDictMapper.updateById(bmsStockDict);
            }
        }
        log.info("*****************仓库同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);


    }

    @Override
    public void synMaterialGroupTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************分组同步开始**************************");
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectList(null);
        for (BmsProductCategoryTb bmsProductCategoryTb : bmsProductCategoryTbList) {
            if (bmsProductCategoryTb.getKdParentId() == null) {
                throw new BusinessException("未给材料类别分配金蝶的分组");
            }
            if (StringUtils.isEmpty(bmsProductCategoryTb.getProductCategoryCode())) {
                throw new BusinessException("未给材料类别配置金蝶存货类别");
            }

            if (bmsProductCategoryTb.getKdNumber() == null) {
                String idStr = kdApiService.execute(OperateEnum.groupSave, bmsProductCategoryTb, PurchaseUnitEnum.default_.name());
                bmsProductCategoryTb.setKdNumber(Integer.valueOf(idStr));
                bmsProductCategoryTbMapper.updateById(bmsProductCategoryTb);
            }
        }
        log.info("*****************分组同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);


    }

    @Override
    public void synSupplierTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************供应商同步开始**************************");
        List<BmsSupplierTb> bmsSupplierTbList = bmsSupplierTbMapper.selectSelective(null);
        bmsSupplierTbList = bmsSupplierTbList.stream().filter(bmsSupplierTb -> bmsSupplierTb.getKdNumber() == null).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(bmsSupplierTbList)) {
            log.info("无新增供应商数据需要同步");
            return;
        }
        List<QuerySupplierDTO> querySupplierDTOList = kdApiService.querySupplier();
        Map<String, List<QuerySupplierDTO>> querySupplierDTOMap = querySupplierDTOList.stream().collect(Collectors.groupingBy(QuerySupplierDTO::getFName));
        for (BmsSupplierTb bmsSupplierTb : bmsSupplierTbList) {
            List<QuerySupplierDTO> querySupplierDTOS = querySupplierDTOMap.get(bmsSupplierTb.getSupplierName());
            if (CollectionUtil.isNotEmpty(querySupplierDTOS)) {
                bmsSupplierTb.setKdNumber(querySupplierDTOS.get(0).getFNumber());
                bmsSupplierTbMapper.updateById(bmsSupplierTb);
            } else {
                log.error("供应商同步,供应商{}没有同步到金蝶数据", bmsSupplierTb.getSupplierName());
            }
        }
        log.info("*****************供应商同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);


    }

    @Override
    public void synMaterialTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************材料同步开始**************************");
        List<BmsProductTb> bmsProductTbList = bmsProductTbMapper.selectList(null);
        for (BmsProductTb bmsProductTb : bmsProductTbList) {
            if (bmsProductTb.getKdNumber() != null && bmsProductTb.getKdNumber() > 0) {
                kdApiService.execute(OperateEnum.materialModify, bmsProductTb, PurchaseUnitEnum.default_.name());
            } else {
                String kdNumber = kdApiService.execute(OperateEnum.materialSave, bmsProductTb, PurchaseUnitEnum.default_.name());
                bmsProductTb.setKdNumber(Integer.valueOf(kdNumber));
                bmsProductTbMapper.updateById(bmsProductTb);
            }
        }
        log.info("*****************材料同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);
    }

    @Override
    public void synInStockTask(String startDate, String endDate) {
        Long startTime = System.currentTimeMillis();
        log.info("*****************入库数据同步开始**************************");
        BmsProductStockInLog selectBmsProductStockInLog = new BmsProductStockInLog();
        selectBmsProductStockInLog.setStartDate(startDate);
        selectBmsProductStockInLog.setEndDate(endDate);
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(selectBmsProductStockInLog);
        if (CollectionUtil.isNotEmpty(bmsProductStockInLogList)) {
            bmsProductStockInLogList = bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getKdNumber() == null).sorted(Comparator.comparing(BmsProductStockInLog::getId)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(bmsProductStockInLogList)) {
                log.info("入库数据已经同步完毕，无需再次同步");
                return;
            }
            for (BmsProductStockInLog bmsProductStockInLog : bmsProductStockInLogList) {
                String kdNumber = kdApiService.execute(OperateEnum.inStockSave, bmsProductStockInLog, bmsProductStockInLog.getUnitCode());
                bmsProductStockInLog.setKdNumber(Integer.valueOf(kdNumber));
                bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
            }
        }
        log.info("*****************入库数据同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);

    }

    @Override
    public void synOutStockTask(String startDate, String endDate) {
        Long startTime = System.currentTimeMillis();
        log.info("*****************出库数据同步开始**************************");
        BmsProductStockOutLog selectBmsProductStockOutLog = new BmsProductStockOutLog();
        selectBmsProductStockOutLog.setStartDate(startDate);
        selectBmsProductStockOutLog.setEndDate(endDate);
        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(selectBmsProductStockOutLog);
        if (CollectionUtil.isNotEmpty(bmsProductStockOutLogList)) {
            bmsProductStockOutLogList = bmsProductStockOutLogList.stream().filter(bmsProductStockOutLog -> bmsProductStockOutLog.getKdNumber() == null).sorted(Comparator.comparing(BmsProductStockOutLog::getId)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(bmsProductStockOutLogList)) {
                log.info("出库数据已经同步完毕，无需再次同步");
                return;
            }
            for (BmsProductStockOutLog bmsProductStockOutLog : bmsProductStockOutLogList) {
                String kdNumber = kdApiService.execute(OperateEnum.outStockSave, bmsProductStockOutLog, bmsProductStockOutLog.getUnitCode());
                bmsProductStockOutLog.setKdNumber(Integer.valueOf(kdNumber));
                bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
            }

        }
        log.info("*****************出库数据同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);

    }

    @Override
    public void synReturnStockTask(String startDate, String endDate) {
        Long startTime = System.currentTimeMillis();
        log.info("*****************退货数据同步开始**************************");
        BmsReturnOrderDetailTb selectBmsReturnOrderDetailTb = new BmsReturnOrderDetailTb();
        selectBmsReturnOrderDetailTb.setStartDate(startDate);
        selectBmsReturnOrderDetailTb.setEndDate(endDate);
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectSelective(selectBmsReturnOrderDetailTb);
        if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList)) {
            bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbList.stream().filter(bmsReturnOrderDetailTb -> bmsReturnOrderDetailTb.getKdNumber() == null).sorted(Comparator.comparing(BmsReturnOrderDetailTb::getId)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(bmsReturnOrderDetailTbList)) {
                log.info("退货数据已经同步完毕，无需再次同步");
                return;
            }
            for (BmsReturnOrderDetailTb bmsReturnOrderDetailTb : bmsReturnOrderDetailTbList) {
                String kdNumber = kdApiService.execute(OperateEnum.returnStockSave, bmsReturnOrderDetailTb, bmsReturnOrderDetailTb.getUnitCode());
                bmsReturnOrderDetailTb.setKdNumber(Integer.valueOf(kdNumber));
                bmsReturnOrderDetailTbMapper.updateById(bmsReturnOrderDetailTb);
            }
        }
        log.info("*****************退货数据同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);

    }

    @Override
    public void executeSynKd(BmsSynKdTaskLog bmsSynKdTaskLog) {
        try {
            synStockTask();
            synMaterialGroupTask();
            synSupplierTask();
            synMaterialTask();
            synProjectTask();
            synInStockTask(bmsSynKdTaskLog.getBeginDate(), bmsSynKdTaskLog.getEndDate());
            synReturnStockTask(bmsSynKdTaskLog.getBeginDate(), bmsSynKdTaskLog.getEndDate());
            synOutStockTask(bmsSynKdTaskLog.getBeginDate(), bmsSynKdTaskLog.getEndDate());
        } catch (Exception e) {
            bmsSynKdTaskLog.setSynStatus(BmsKdSynStatusEnum.fail.name());
            bmsSynKdTaskLog.setFailReason(JSONUtil.toJsonStr(e));
            bmsSynKdTaskLogMapper.updateById(bmsSynKdTaskLog);
        }
        bmsSynKdTaskLog.setSynStatus(BmsKdSynStatusEnum.success.name());
        bmsSynKdTaskLogMapper.updateById(bmsSynKdTaskLog);
    }
}
