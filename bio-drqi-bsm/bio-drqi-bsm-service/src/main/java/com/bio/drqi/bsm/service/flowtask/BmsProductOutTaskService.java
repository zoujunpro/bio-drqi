package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.bsm.dto.BmsProductOutDTO;
import com.bio.drqi.bsm.enums.OutTypeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
        bmsProductStockOutLog.setProductPrice(bmsProductStockInLogList.get(0).getProductPrice());
        bmsProductStockOutLog.setOutAmount(bmsProductStockOutLog.getProductPrice().multiply(bmsProductStockOutLog.getOutNumber()));
        bmsProductStockOutLogMapper.insert(bmsProductStockOutLog);
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
