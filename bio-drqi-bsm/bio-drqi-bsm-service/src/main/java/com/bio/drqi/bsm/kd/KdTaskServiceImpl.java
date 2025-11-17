package com.bio.drqi.bsm.kd;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.enums.BmsKdSynStatusEnum;
import com.bio.drqi.bsm.enums.PurchaseUnitEnum;
import com.bio.drqi.bsm.kd.dto.QuerySupplierDTO;
import com.bio.drqi.bsm.kd.enums.OperateEnum;
import com.bio.drqi.bsm.kd.service.KdApiService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
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
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsSynKdTaskLogMapper bmsSynKdTaskLogMapper;


    public void synProjectTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************项目同步开始**************************");
        List<BmsProjectDict> bmsProjectDictList = bmsProjectDictMapper.selectList(null);
        Map<String, List<BmsProjectDict>> mapList = bmsProjectDictList.stream().filter(bmsProjectDict -> StringUtils.isNotEmpty(bmsProjectDict.getKdProjectType())).collect(Collectors.groupingBy(BmsProjectDict::getKdProjectCode));
        mapList.forEach((kdProjectCode, list) -> {
            if (StringUtils.isNotEmpty(kdProjectCode)) {
                BmsProjectDict bmsProjectDict = list.get(0);
                if (StringUtils.isEmpty(bmsProjectDict.getKdNumber())) {
                    String kdNumber = null;
                    try {
                        kdNumber = kdApiService.execute(OperateEnum.projectSave, bmsProjectDict, PurchaseUnitEnum.default_.name());
                    } catch (Exception e) {
                        kdNumber = kdApiService.execute(OperateEnum.projectQuery, bmsProjectDict, PurchaseUnitEnum.default_.name());
                        if (kdNumber == null) {
                            log.error("项目同步失败,{}", e);
                            throw new BusinessException(e.getMessage());
                        }
                    }
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
            String kdNumber = null;
            if (StringUtils.isNotEmpty(bmsStockDict.getKdNumber())) {
                kdNumber = kdApiService.execute(OperateEnum.stockModify, bmsStockDict, bmsStockDict.getUnitCode());
            } else {
                try {
                    kdNumber = kdApiService.execute(OperateEnum.stockSave, bmsStockDict, bmsStockDict.getUnitCode());
                } catch (Exception e) {
                    kdNumber = kdApiService.execute(OperateEnum.stockQuery, bmsStockDict, bmsStockDict.getUnitCode());
                    if (kdNumber == null) {
                        log.error("仓库同步失败,{}", e);
                        throw new BusinessException(e.getMessage());
                    }
                }
                bmsStockDict.setKdNumber(kdNumber);
                bmsStockDictMapper.updateById(bmsStockDict);
            }
        }
        //同步完毕后自检
        checkAftSynStockTask();
        log.info("*****************仓库同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);


    }

    @Override
    public void synMaterialGroupTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************分组同步开始**************************");
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
        //过滤出需要同步金蝶的数据
        bmsProductCategoryTbList = bmsProductCategoryTbList.stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdParentId() != null && bmsProductCategoryTb.getKdCategoryCode() != null).collect(Collectors.toList());
        for (BmsProductCategoryTb bmsProductCategoryTb : bmsProductCategoryTbList) {
            if (bmsProductCategoryTb.getKdParentId() == null) {
                throw new BusinessException("未给材料类别分配金蝶的分组");
            }
            if (StringUtils.isEmpty(bmsProductCategoryTb.getProductCategoryCode())) {
                throw new BusinessException("未给材料类别配置金蝶存货类别");
            }

            if (bmsProductCategoryTb.getKdNumber() == null) {
                String idStr = null;
                try {
                    idStr = kdApiService.execute(OperateEnum.groupSave, bmsProductCategoryTb, PurchaseUnitEnum.default_.name());
                } catch (Exception e) {
                    idStr = kdApiService.execute(OperateEnum.groupQuery, bmsProductCategoryTb, PurchaseUnitEnum.default_.name());
                    if (idStr == null) {
                        log.error("分组同步失败,{}", e);
                        throw new BusinessException(e.getMessage());
                    }

                }
                bmsProductCategoryTb.setKdNumber(idStr);
                bmsProductCategoryTbMapper.updateById(bmsProductCategoryTb);
            }
        }
        checkAftSynMaterialGroupTask();
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
        checkAftSynSupplierTask();
        log.info("*****************供应商同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);


    }

    @Override
    public void synMaterialTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************材料同步开始**************************");
        checkBefSynMaterialTask();
        List<String> bmsProductCategoryCodeList = bmsProductCategoryTbMapper.selectList(null).stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdNumber() != null).map(BmsProductCategoryTb::getProductCategoryCode).collect(Collectors.toList());
        List<BmsProductTb> bmsProductTbList = bmsProductTbMapper.selectList(null).stream().filter(bmsProductTb -> bmsProductCategoryCodeList.contains(bmsProductTb.getProductCategoryCode())).collect(Collectors.toList());
        for (BmsProductTb bmsProductTb : bmsProductTbList) {
            if (StringUtils.isNotEmpty(bmsProductTb.getKdNumber())) {
                kdApiService.execute(OperateEnum.materialModify, bmsProductTb, PurchaseUnitEnum.default_.name());
            } else {
                String kdNumber = null;
                try {
                    kdNumber = kdApiService.execute(OperateEnum.materialSave, bmsProductTb, PurchaseUnitEnum.default_.name());
                } catch (Exception e) {
                    kdNumber = kdApiService.execute(OperateEnum.materialQuery, bmsProductTb, PurchaseUnitEnum.default_.name());
                    if (kdNumber == null) {
                        log.error("材料同步失败{}", e);
                        throw new BusinessException(e.getMessage());
                    }
                }
                bmsProductTb.setKdNumber(kdNumber);
                bmsProductTbMapper.updateById(bmsProductTb);
            }
        }
        checkAftSynMaterialTask();
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
        //过滤出不需要同步的项目
        List<String> projectCodeList = bmsProjectDictMapper.selectList(null).stream().filter(bmsProjectDict -> StringUtils.isNotEmpty(bmsProjectDict.getKdNumber())).map(BmsProjectDict::getProjectCode).collect(Collectors.toList());
        //过滤出不需要同步的耗材
        List<String> bmsProductCategoryCodeList = bmsProductCategoryTbMapper.selectList(null).stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdNumber() != null).map(BmsProductCategoryTb::getProductCategoryCode).collect(Collectors.toList());

        bmsProductStockInLogList = bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductCategoryCodeList.contains(bmsProductStockInLog.getProductCategoryCode()) && projectCodeList.contains(bmsProductStockInLog.getProjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(bmsProductStockInLogList)) {
            log.info("入库数据已经同步完毕，无需再次同步");
            return;
        }
        for (BmsProductStockInLog bmsProductStockInLog : bmsProductStockInLogList) {
            String kdNumber = null;
            try {
                kdNumber = kdApiService.execute(OperateEnum.inStockSave, bmsProductStockInLog, bmsProductStockInLog.getUnitCode());
            } catch (Exception e) {
                kdNumber = kdApiService.execute(OperateEnum.inStockQuery, bmsProductStockInLog, bmsProductStockInLog.getUnitCode());
                if (kdNumber == null) {
                    log.error("入库数据同步失败{}", e);
                    throw new BusinessException(e.getMessage());
                }
            }
            bmsProductStockInLog.setKdNumber(kdNumber);
            bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
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
        //过滤出需要同步的数据
        List<String> bmsProductCategoryCodeList = bmsProductCategoryTbMapper.selectList(null).stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdNumber() != null).map(BmsProductCategoryTb::getProductCategoryCode).collect(Collectors.toList());
        bmsProductStockOutLogList = bmsProductStockOutLogList.stream().filter(bmsProductStockOutLog -> bmsProductCategoryCodeList.contains(bmsProductStockOutLog.getProductCategoryCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bmsProductStockOutLogList)) {
            bmsProductStockOutLogList = bmsProductStockOutLogList.stream().filter(bmsProductStockOutLog -> bmsProductStockOutLog.getKdNumber() == null).sorted(Comparator.comparing(BmsProductStockOutLog::getId)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(bmsProductStockOutLogList)) {
                log.info("出库数据已经同步完毕，无需再次同步");
                return;
            }
            for (BmsProductStockOutLog bmsProductStockOutLog : bmsProductStockOutLogList) {
                String kdNumber = null;
                try {
                    kdNumber = kdApiService.execute(OperateEnum.outStockSave, bmsProductStockOutLog, bmsProductStockOutLog.getUnitCode());
                } catch (Exception e) {
                    kdNumber = kdApiService.execute(OperateEnum.outStockQuery, bmsProductStockOutLog, bmsProductStockOutLog.getUnitCode());
                    if (kdNumber == null) {
                        log.error("出库数据同步失败{}", e);
                        throw new BusinessException(e.getMessage());
                    }

                }
                bmsProductStockOutLog.setKdNumber(kdNumber);
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
        //过滤出不需要同步的项目
        List<String> projectCodeList = bmsProjectDictMapper.selectList(null).stream().filter(bmsProjectDict -> StringUtils.isNotEmpty(bmsProjectDict.getKdProjectCode())).map(BmsProjectDict::getProjectCode).collect(Collectors.toList());
        List<String> bmsProductCategoryCodeList = bmsProductCategoryTbMapper.selectList(null).stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdNumber() != null).map(BmsProductCategoryTb::getProductCategoryCode).collect(Collectors.toList());
        bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbList.stream().filter(bmsReturnOrderDetailTb -> bmsProductCategoryCodeList.contains(bmsReturnOrderDetailTb.getProductCategoryCode()) && projectCodeList.contains(bmsReturnOrderDetailTb.getProjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList)) {
            bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbList.stream().filter(bmsReturnOrderDetailTb -> bmsReturnOrderDetailTb.getKdNumber() == null).sorted(Comparator.comparing(BmsReturnOrderDetailTb::getId)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(bmsReturnOrderDetailTbList)) {
                log.info("退货数据已经同步完毕，无需再次同步");
                return;
            }
            for (BmsReturnOrderDetailTb bmsReturnOrderDetailTb : bmsReturnOrderDetailTbList) {
                String kdNumber = null;
                try {
                    kdNumber = kdApiService.execute(OperateEnum.returnStockSave, bmsReturnOrderDetailTb, bmsReturnOrderDetailTb.getUnitCode());
                } catch (Exception e) {
                    kdNumber = kdApiService.execute(OperateEnum.returnStockQuery, bmsReturnOrderDetailTb, bmsReturnOrderDetailTb.getUnitCode());
                    if (kdNumber == null) {
                        log.error("退货数据同步失败{}", e);
                        throw new BusinessException(e.getMessage());
                    }
                }
                bmsReturnOrderDetailTb.setKdNumber(kdNumber);
                bmsReturnOrderDetailTbMapper.updateById(bmsReturnOrderDetailTb);
            }
        }
        log.info("*****************退货数据同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);

    }

    @Override
    public void synMoveStockTask(String startDate, String endDate) {
        Long startTime = System.currentTimeMillis();
        log.info("*****************调拨数据同步开始**************************");
        BmsMoveOrderDetailTb selectBmsMoveOrderDetailTb = new BmsMoveOrderDetailTb();
        selectBmsMoveOrderDetailTb.setStartDate(startDate);
        selectBmsMoveOrderDetailTb.setEndDate(endDate);
        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectSelective(selectBmsMoveOrderDetailTb);
        //过滤出不需要同步的耗材
        List<String> bmsProductCategoryCodeList = bmsProductCategoryTbMapper.selectList(null).stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdNumber() != null).map(BmsProductCategoryTb::getProductCategoryCode).collect(Collectors.toList());
        bmsMoveOrderDetailTbList.stream().filter(bmsMoveOrderDetailTb -> bmsProductCategoryCodeList.contains(bmsMoveOrderDetailTb.getProductCategoryCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bmsMoveOrderDetailTbList)) {
            bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbList.stream().filter(bmsMoveOrderDetailTb -> bmsMoveOrderDetailTb.getKdNumber() == null).sorted(Comparator.comparing(BmsMoveOrderDetailTb::getId)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(bmsMoveOrderDetailTbList)) {
                log.info("调拨数据已经同步完毕，无需再次同步");
                return;
            }
            for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb : bmsMoveOrderDetailTbList) {
                String kdNumber = null;
                try {
                    kdNumber = kdApiService.execute(OperateEnum.moveStockSave, bmsMoveOrderDetailTb, bmsMoveOrderDetailTb.getUnitCode());
                } catch (Exception e) {
                    kdNumber = kdApiService.execute(OperateEnum.moveStockQuery, bmsMoveOrderDetailTb, bmsMoveOrderDetailTb.getUnitCode());
                    if (kdNumber == null) {
                        log.error("调拨数据同步失败{}", e);
                        throw new BusinessException(e.getMessage());
                    }
                }
                bmsMoveOrderDetailTb.setKdNumber(kdNumber);
                bmsMoveOrderDetailTbMapper.updateById(bmsMoveOrderDetailTb);
            }
        }
        log.info("*****************调拨数据同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);

    }

    @Override
    public void executeSynKd(BmsSynKdTaskLog bmsSynKdTaskLog) {
        try {
            synStockTask();
            synMaterialGroupTask();
            synSupplierTask();
            synProjectTask();
            synMaterialTask();
            synInStockTask(bmsSynKdTaskLog.getBeginDate(), bmsSynKdTaskLog.getEndDate());
            synMoveStockTask(bmsSynKdTaskLog.getBeginDate(), bmsSynKdTaskLog.getEndDate());
            synReturnStockTask(bmsSynKdTaskLog.getBeginDate(), bmsSynKdTaskLog.getEndDate());
            synOutStockTask(bmsSynKdTaskLog.getBeginDate(), bmsSynKdTaskLog.getEndDate());

            bmsSynKdTaskLog.setSynStatus(BmsKdSynStatusEnum.success.name());
            bmsSynKdTaskLogMapper.updateById(bmsSynKdTaskLog);
        } catch (Exception e) {
            log.error("执行同步金蝶数据失败", e);
            bmsSynKdTaskLog.setSynStatus(BmsKdSynStatusEnum.fail.name());
            bmsSynKdTaskLog.setFailReason(JSONUtil.toJsonStr(e));
            bmsSynKdTaskLogMapper.updateById(bmsSynKdTaskLog);
        }

    }

    private void checkAftSynMaterialTask() {
        List<BmsProductTb> bmsProductTbList = bmsProductTbMapper.selectList(null);
        List<String> bmsProductCategoryCodeList = bmsProductCategoryTbMapper.selectList(null).stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdNumber() != null).map(BmsProductCategoryTb::getProductCategoryCode).collect(Collectors.toList());
        bmsProductTbList = bmsProductTbList.stream().filter(bmsProductTb -> bmsProductCategoryCodeList.contains(bmsProductTb.getProductCategoryCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bmsProductTbList.stream().filter(bmsProductTb -> bmsProductTb.getKdNumber() == null).collect(Collectors.toList()))) {
            throw new BusinessException("有部分商品未同步到金蝶");
        }
    }

    private void checkBefSynMaterialTask() {
        List<BmsProductTb> bmsProductTbList = bmsProductTbMapper.selectList(null);
        if (CollectionUtil.isNotEmpty(bmsProductTbList.stream().filter(bmsProductTb -> bmsProductTb.getProductCategoryCode() == null).collect(Collectors.toList()))) {
            throw new BusinessException("有部分商品的耗材未配置类别");
        }
    }

    private void checkAftSynSupplierTask() {
        List<BmsSupplierTb> bmsSupplierTbList = bmsSupplierTbMapper.selectSelective(null);
        bmsSupplierTbList = bmsSupplierTbList.stream().filter(bmsSupplierTb -> bmsSupplierTb.getKdNumber() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bmsSupplierTbList)) {
            throw new BusinessException("部分供应商未同步");
        }

    }

    private void checkAftSynMaterialGroupTask() {
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
        //过滤出需要同步金蝶的数据
        bmsProductCategoryTbList = bmsProductCategoryTbList.stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdParentId() != null && bmsProductCategoryTb.getKdCategoryCode() != null).collect(Collectors.toList());
        //   找出未同步到金蝶的耗材数据
        bmsProductCategoryTbList = bmsProductCategoryTbList.stream().filter(bmsProductCategoryTb -> bmsProductCategoryTb.getKdNumber() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bmsProductCategoryTbList)) {
            throw new BusinessException("部分耗材类别有未同步到金蝶");
        }
    }

    private void checkAftSynStockTask() {
        List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
        bmsStockDictList = bmsStockDictList.stream().filter(bmsStockDict -> bmsStockDict.getKdNumber() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bmsStockDictList)) {
            throw new BusinessException("库存有数据未同步到金蝶");
        }
    }
}
