package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.bsm.dto.BmsProductOutDTO;
import com.bio.drqi.bsm.enums.PurchaseUnitEnum;
import com.bio.drqi.bsm.enums.OutTypeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.*;
import com.bio.flow.dto.BioHtmlModelDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品出库
 */

@Service("bms_product_out")
public class BmsProductOutTaskService extends AbstractBsmBaseTaskService {

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;


    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsStockLocationDictMapper bmsStockLocationDictMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        List<BmsProductOutDTO> bmsProductOutDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), BmsProductOutDTO.class);
        for (BmsProductOutDTO bmsProductOutDTO : bmsProductOutDTOList) {
            ValidatorUtil.validator(bmsProductOutDTO);
            BeanUtils.trimFiledSpace(bmsProductOutDTO);
        }
        if (CollectionUtil.isEmpty(bmsProductOutDTOList)) {
            throw new BusinessException("请选择出库数据");
        }
        for (BmsProductOutDTO bmsProductOutDTO : bmsProductOutDTOList) {
            BeanUtils.trimFiledSpace(bmsProductOutDTO);
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByUniqueCode(bmsProductOutDTO.getUniqueCode());
            if (bmsProductStockTb == null) {
                throw new BusinessException("数据库中不存在此商品信息");
            }
            if (bmsProductStockTb.getCurrentStockNumber().compareTo(bmsProductOutDTO.getNumber()) < 0) {
                throw new BusinessException("批次号为：" + bmsProductOutDTO.getBatchNo() + "的" + bmsProductOutDTO.getProductName() + "库存不足");
            }
        }
        List<String> productNameList = bmsProductOutDTOList.stream().map(BmsProductOutDTO::getProductName).collect(Collectors.toList());
        StringBuilder productNames = new StringBuilder();
        for (String productName : productNameList) {
            productNames.append(productName).append(";");
        }
        bioTaskDtlTb.setTaskDesc(productNames.substring(0, productNames.length() - 1));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            List<BmsProductOutDTO> bmsProductOutDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), BmsProductOutDTO.class);
            for (BmsProductOutDTO bmsProductOutDTO : bmsProductOutDTOList) {
                //扣减库存
                doOutStock(bioTaskDtlTb, bmsProductOutDTO);

            }
        }

    }

    public void doOutStock(BioTaskDtlTb bioTaskDtlTb, BmsProductOutDTO bmsProductOutDTO) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByUniqueCode(bmsProductOutDTO.getUniqueCode());
        bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber().subtract(bmsProductOutDTO.getNumber()));
        bmsProductStockTb.setTotalOutNumber(bmsProductStockTb.getTotalOutNumber().add(bmsProductOutDTO.getNumber()));
        bmsProductStockTbMapper.updateById(bmsProductStockTb);
        //生成出库记录
        //找出入库记录
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BmsProductStockInLog.builder().productInnerCode(bmsProductStockTb.getProductInnerCode()).unitCode(bmsProductStockTb.getUnitCode()).batchNo(bmsProductStockTb.getBatchNo()).endDate(DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN)).build());
        bmsProductStockInLogList = bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getProductPrice().doubleValue() > 0).collect(Collectors.toList());
        BmsProductStockOutLog bmsProductStockOutLog = new BmsProductStockOutLog();
        bmsProductStockOutLog.setProductName(bmsProductStockTb.getProductName());
        bmsProductStockOutLog.setProductOutCode(bmsProductStockTb.getProductOutCode());
        bmsProductStockOutLog.setProductCategoryCode(bmsProductStockTb.getProductCategoryCode());
        bmsProductStockOutLog.setBrandCode(bmsProductStockTb.getBrandCode());
        bmsProductStockOutLog.setProductSpecs(bmsProductStockTb.getProductSpecs());
        bmsProductStockOutLog.setBatchNo(bmsProductStockTb.getBatchNo());
        bmsProductStockOutLog.setOutNumber(bmsProductOutDTO.getNumber());
        bmsProductStockOutLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        bmsProductStockOutLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        bmsProductStockOutLog.setCreateTime(new Date());
        bmsProductStockOutLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        bmsProductStockOutLog.setRemark(bmsProductOutDTO.getRemark());
        bmsProductStockOutLog.setOutType(OutTypeEnum.TYPE_1.code);
        bmsProductStockOutLog.setUnitCode(bmsProductOutDTO.getUnitCode());
        bmsProductStockOutLog.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
        bmsProductStockOutLog.setUniqueCode(bmsProductStockTb.getUniqueCode());
        bmsProductStockOutLog.setSupplierCode(bmsProductStockTb.getSupplierCode());
        bmsProductStockOutLog.setProduceDate(bmsProductStockTb.getProduceDate());
        bmsProductStockOutLog.setExpirationDate(bmsProductStockTb.getExpirationDate());
        bmsProductStockOutLog.setStockCode(bmsProductStockTb.getStockCode());
        bmsProductStockOutLog.setProductPrice(CollectionUtil.isNotEmpty(bmsProductStockInLogList)?bmsProductStockInLogList.get(0).getProductPrice():new BigDecimal("0"));
        bmsProductStockOutLog.setOutAmount(bmsProductStockOutLog.getProductPrice().multiply(bmsProductStockOutLog.getOutNumber()));
        bmsProductStockOutLog.setPayType(bmsProductStockTb.getPayType());
        bmsProductStockOutLogMapper.insert(bmsProductStockOutLog);
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        List<BmsProductOutDTO> dtoList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), BmsProductOutDTO.class);
        if (CollectionUtil.isEmpty(dtoList)) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        List<String> headers = java.util.Arrays.asList("商品名称", "规格", "批次号", "出库数量", "单位", "库房", "当前库存", "累计出库", "库位", "备注");
        List<Map<String, Object>> rows = new ArrayList<>();
        for (BmsProductOutDTO item : dtoList) {
            BmsProductStockTb stockTb = bmsProductStockTbMapper.selectOneByUniqueCode(item.getUniqueCode());
            BmsStockDict stockDict = stockTb == null || StringUtils.isEmpty(stockTb.getStockCode()) ? null : bmsStockDictMapper.selectOneByStockCode(stockTb.getStockCode());
            Map<String, String> locationNameMap = stockTb == null ? Collections.emptyMap() :
                    bmsStockLocationDictMapper.selectAllByStockCode(stockTb.getStockCode()).stream()
                            .collect(Collectors.toMap(BmsStockLocationDict::getLocationNumber, BmsStockLocationDict::getStockName, (left, right) -> left));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("商品名称", item.getProductName());
            row.put("规格", item.getProductSpecs());
            row.put("批次号", item.getBatchNo());
            row.put("出库数量", decimalText(item.getNumber()));
            row.put("单位", unitName(stockTb == null ? item.getUnitCode() : stockTb.getUnitCode()));
            row.put("库房", stockDict == null ? item.getStockCode() : stockDict.getStockName());
            row.put("当前库存", stockTb == null ? "" : decimalText(stockTb.getCurrentStockNumber()));
            row.put("累计出库", stockTb == null ? "" : decimalText(stockTb.getTotalOutNumber()));
            row.put("库位", stockTb == null || StringUtils.isEmpty(stockTb.getStockLocationNumber()) ? "" : locationText(JSONUtil.toList(stockTb.getStockLocationNumber(), String.class), locationNameMap));
            row.put("备注", item.getRemark());
            rows.add(row);
        }
        sections.add(buildTableSection("出库明细", headers, rows));
        return sections;
    }

    private String locationText(List<String> locationNumberList, Map<String, String> locationNameMap) {
        if (CollectionUtil.isEmpty(locationNumberList)) {
            return "";
        }
        return locationNumberList.stream()
                .filter(StringUtils::isNotEmpty)
                .map(locationNumber -> {
                    String stockName = locationNameMap.get(locationNumber);
                    return StringUtils.isEmpty(stockName) ? locationNumber : stockName + "/" + locationNumber;
                })
                .collect(Collectors.joining("、"));
    }

    private String decimalText(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private String unitName(String unitCode) {
        if (StringUtils.isEmpty(unitCode)) {
            return "";
        }
        if (PurchaseUnitEnum.beijing.name().equals(unitCode)) {
            return "北京";
        }
        if (PurchaseUnitEnum.tianjin.name().equals(unitCode)) {
            return "天津";
        }
        if (PurchaseUnitEnum.default_.name().equals(unitCode)) {
            return "默认";
        }
        return unitCode;
    }
}
