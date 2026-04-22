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
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品入库
 */

@Service("bms_product_input")
@Slf4j
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

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

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
        if (CollectionUtil.isEmpty(bmsProductInputDTO.getOrderDetailList())) {
            throw new BusinessException("没有入库数据");
        }

        for (BmsProductInputDTO.OrderDetail orderDetail : bmsProductInputDTO.getOrderDetailList()) {
            ValidatorUtil.validator(orderDetail);
            BeanUtils.trimFiledSpace(orderDetail);
            BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectOneByOrderDetailNum(orderDetail.getOrderDetailNum());
            if (Objects.equals(bmsOrderDetailTb.getPurchaseNumber(), bmsOrderDetailTb.getReceiveNumber().intValue())) {
                throw new BusinessException("该耗材已经全部到货");
            }
            if (bmsOrderDetailTb.getPurchaseNumber().subtract(bmsOrderDetailTb.getReceiveNumber()).compareTo(orderDetail.getNumber()) < 0) {
                throw new BusinessException("入库数量已经大于剩余待入库数量");
            }
            if (CollectionUtil.isNotEmpty(orderDetail.getStockLocationNumberList())) {
                orderDetail.getStockLocationNumberList().forEach(stockLocationNumber -> {
                    BmsStockLocationDict bmsStockLocationDict = bmsStockLocationDictMapper.selectOneByUnitCodeAndLocationNumber(bmsOrderDetailTb.getApplyUnitCode(), stockLocationNumber);
                    if (bmsStockLocationDict == null) {
                        log.error("库存信息不存在 unitCode={},stockLocationNumber={}", bmsProductInputDTO.getApplyUnitCode(), stockLocationNumber);
                        throw new BusinessException("库存信息不存在");
                    }
                });
            }
            orderDetail.setProjectCode(bmsOrderDetailTb.getProjectCode());
            orderDetail.setBrandCode(bmsOrderDetailTb.getBrandCode());
            orderDetail.setProductName(bmsOrderDetailTb.getProductName());
            orderDetail.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
            orderDetail.setProductOutCode(bmsOrderDetailTb.getProductOutCode());
            orderDetail.setPurchasePrice(bmsOrderDetailTb.getPurchasePrice());
            orderDetail.setPurchaseNumber(bmsOrderDetailTb.getPurchaseNumber());
            orderDetail.setPayAmount(bmsOrderDetailTb.getPayAmount());
            orderDetail.setProductCategoryCode(bmsOrderDetailTb.getProductCategoryCode());
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
                ValidatorUtil.validator(inputOrderDetail);
                BeanUtils.trimFiledSpace(inputOrderDetail);
                BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectOneByOrderDetailNum(inputOrderDetail.getOrderDetailNum());
                String batchNo = StringUtils.isEmpty(inputOrderDetail.getBatchNo()) ? "N/A" : inputOrderDetail.getBatchNo();
                // 入库库存updateOrInsertBmsProductStock
                BmsProductStockTb bmsProductStockTb = updateOrInsertBmsProductStock(inputOrderDetail, bmsOrderDetailTb, batchNo);
                //记录入库记录
                insertBmsProductStockInLog(bioTaskDtlTb, inputOrderDetail, bmsOrderDetailTb, bmsProductStockTb);

                //订单明细接收数量增加
                bmsOrderDetailTb.setReceiveNumber(bmsOrderDetailTb.getReceiveNumber().add(inputOrderDetail.getNumber()));
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
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByProductInnerCodeAndUnitCodeAndBatchNoAndStockCodeAndPayType(bmsOrderDetailTb.getProductInnerCode(), bmsOrderDetailTb.getApplyUnitCode(), batchNo, inputOrderDetail.getStockCode(),bmsOrderDetailTb.getPayType());
        if (bmsProductStockTb == null) {
            bmsProductStockTb = new BmsProductStockTb();
            bmsProductStockTb.setProductName(bmsOrderDetailTb.getProductName());
            bmsProductStockTb.setProductOutCode(bmsOrderDetailTb.getProductOutCode());
            bmsProductStockTb.setProductCategoryCode(bmsOrderDetailTb.getProductCategoryCode());
            bmsProductStockTb.setBrandCode(bmsOrderDetailTb.getBrandCode());
            bmsProductStockTb.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
            bmsProductStockTb.setBatchNo(batchNo);
            bmsProductStockTb.setTotalStoreNumber(inputOrderDetail.getNumber());
            bmsProductStockTb.setCurrentStockNumber(inputOrderDetail.getNumber());
            bmsProductStockTb.setTotalOutNumber(new BigDecimal(0));
            bmsProductStockTb.setUnitCode(bmsOrderDetailTb.getApplyUnitCode());
            bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(inputOrderDetail.getStockLocationNumberList()));
            bmsProductStockTb.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
            bmsProductStockTb.setSupplierCode(bmsOrderDetailTb.getSupplierCode());
            bmsProductStockTb.setProductInnerCode(bmsOrderDetailTb.getProductInnerCode());
            bmsProductStockTb.setUniqueCode(IdUtils.simpleUUID());
            bmsProductStockTb.setProduceDate(inputOrderDetail.getProduceDate());
            bmsProductStockTb.setExpirationDate(inputOrderDetail.getExpirationDate());
            bmsProductStockTb.setStockCode(inputOrderDetail.getStockCode());
            bmsProductStockTb.setProductPrice(inputOrderDetail.getPurchasePrice());
            bmsProductStockTb.setReturnNumber(new BigDecimal(0));
            bmsProductStockTb.setPayType(bmsOrderDetailTb.getPayType());
            bmsProductStockTbMapper.insert(bmsProductStockTb);
        } else {
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber().add(inputOrderDetail.getNumber()));
            bmsProductStockTb.setTotalStoreNumber(bmsProductStockTb.getTotalStoreNumber().add(inputOrderDetail.getNumber()));
            if (CollectionUtil.isNotEmpty(inputOrderDetail.getStockLocationNumberList())) {
                List<String> currentStockLocationNumberList = JSONUtil.toList(bmsProductStockTb.getStockLocationNumber(), String.class);
                if (CollectionUtil.isEmpty(currentStockLocationNumberList)) {
                    currentStockLocationNumberList = new ArrayList<>();
                }
                currentStockLocationNumberList.addAll(inputOrderDetail.getStockLocationNumberList());
                bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(currentStockLocationNumberList));
            }
            if(inputOrderDetail.getPurchasePrice().doubleValue()>0){
                bmsProductStockTb.setProductPrice(inputOrderDetail.getPurchasePrice());
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
        bmsProductStockInLog.setBrandCode(bmsOrderDetailTb.getBrandCode());
        bmsProductStockInLog.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
        bmsProductStockInLog.setBatchNo(inputOrderDetail.getBatchNo());
        bmsProductStockInLog.setProjectCode(bmsOrderDetailTb.getProjectCode());
        bmsProductStockInLog.setProductPrice(bmsOrderDetailTb.getPurchasePrice());
        bmsProductStockInLog.setStoreNumber(inputOrderDetail.getNumber());
        bmsProductStockInLog.setStoreAmount(bmsOrderDetailTb.getPurchasePrice().multiply(inputOrderDetail.getNumber()));
        bmsProductStockInLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        bmsProductStockInLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        bmsProductStockInLog.setCreateTime(new Date());
        bmsProductStockInLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        bmsProductStockInLog.setOrderNum(bmsOrderDetailTb.getOrderNum());
        bmsProductStockInLog.setStockLocationNumber(JSONUtil.toJsonStr(inputOrderDetail.getStockLocationNumberList()));
        bmsProductStockInLog.setUnitCode(bmsOrderDetailTb.getApplyUnitCode());
        bmsProductStockInLog.setUniqueCode(bmsProductStockTb.getUniqueCode());
        bmsProductStockInLog.setSupplierCode(bmsProductStockTb.getSupplierCode());
        bmsProductStockInLog.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
        bmsProductStockInLog.setProduceDate(inputOrderDetail.getProduceDate());
        bmsProductStockInLog.setExpirationDate(inputOrderDetail.getExpirationDate());
        bmsProductStockInLog.setTaxRate(bmsOrderDetailTb.getTaxRate());
        bmsProductStockInLog.setStockCode(inputOrderDetail.getStockCode());
        bmsProductStockInLog.setReturnNumber(new BigDecimal(0));
        bmsProductStockInLog.setPayType(bmsOrderDetailTb.getPayType());
        bmsProductStockInLogMapper.insert(bmsProductStockInLog);
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        BmsProductInputDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsProductInputDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("订单编号", dto.getOrderNum()));
        applyFields.add(buildField("采购部门", dto.getPurchaseDepartment()));
        applyFields.add(buildField("申请单位", dto.getApplyUnitName()));
        applyFields.add(buildField("直接出库", BioBsmContents.Y.equals(dto.getOutStockFlag()) ? "是" : "否"));
        sections.add(buildFieldSection("申请信息", applyFields));

        Map<String, String> projectNameMap = bmsProjectDictMapper.selectAllOrderByIdDesc().stream()
                .collect(Collectors.toMap(BmsProjectDict::getProjectCode, BmsProjectDict::getProjectName, (left, right) -> left));
        Map<String, String> supplierNameMap = bmsSupplierTbMapper.selectSelective(new BmsSupplierTb()).stream()
                .collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName, (left, right) -> left));
        Map<String, String> brandNameMap = bmsBrandTbMapper.selectSelective(new BmsBrandTb()).stream()
                .collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName, (left, right) -> left));
        Map<String, String> locationNameMap = bmsStockLocationDictMapper.selectAllByUnitCode(dto.getApplyUnitCode()).stream()
                .collect(Collectors.toMap(BmsStockLocationDict::getLocationNumber, BmsStockLocationDict::getStockName, (left, right) -> left));

        List<String> headers = Arrays.asList("订单明细号", "项目", "供应商", "品牌", "商品名称", "规格", "批次号", "入库数量", "采购单价", "库房", "库位", "生产日期", "有效期", "付款类型");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dto.getOrderDetailList())) {
            for (BmsProductInputDTO.OrderDetail item : dto.getOrderDetailList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("订单明细号", item.getOrderDetailNum());
                row.put("项目", defaultText(projectNameMap.get(item.getProjectCode()), item.getProjectCode()));
                row.put("供应商", defaultText(supplierNameMap.get(item.getSupplierCode()), item.getSupplierCode()));
                row.put("品牌", defaultText(brandNameMap.get(item.getBrandCode()), item.getBrandName()));
                row.put("商品名称", item.getProductName());
                row.put("规格", item.getProductSpecs());
                row.put("批次号", item.getBatchNo());
                row.put("入库数量", decimalText(item.getNumber()));
                row.put("采购单价", decimalText(item.getPurchasePrice()));
                row.put("库房", item.getStockCode());
                row.put("库位", locationText(item.getStockLocationNumberList(), locationNameMap));
                row.put("生产日期", item.getProduceDate());
                row.put("有效期", item.getExpirationDate());
                row.put("付款类型", item.getPayType());
                rows.add(row);
            }
        }
        sections.add(buildTableSection("入库明细", headers, rows));
        return sections;
    }

    private String locationText(List<String> locationNumberList, Map<String, String> locationNameMap) {
        if (CollectionUtil.isEmpty(locationNumberList)) {
            return "";
        }
        return locationNumberList.stream()
                .map(locationNumber -> {
                    String stockName = locationNameMap.get(locationNumber);
                    return StringUtils.isEmpty(stockName) ? locationNumber : stockName + "/" + locationNumber;
                })
                .collect(Collectors.joining("、"));
    }

    private String decimalText(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private String defaultText(String first, String fallback) {
        return StringUtils.isNotEmpty(first) ? first : fallback;
    }
}
