package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.req.BmsStockBroadCountStockReqDTO;
import com.bio.drqi.bsm.rsp.*;
import com.bio.drqi.bsm.service.BmsStockBroadService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsStockBroadServiceImpl implements BmsStockBroadService {


    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;


    @Override
    public BmsStockBroadCountStockRspDTO countStock(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        BmsStockBroadCountStockRspDTO bmsStockBroadCountStockRspDTO = new BmsStockBroadCountStockRspDTO();
        bmsStockBroadCountStockRspDTO.setTotalStockInAmount(bmsProductStockInLogMapper.selectSumAmount(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsProductStockInLog.class)));
        bmsStockBroadCountStockRspDTO.setTotalStockOutAmount(bmsProductStockOutLogMapper.selectSumAmount(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsProductStockOutLog.class)));
        bmsStockBroadCountStockRspDTO.setTotalStockReturnAmount(bmsReturnOrderDetailTbMapper.selectSumAmount(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsReturnOrderDetailTb.class)));
        return bmsStockBroadCountStockRspDTO;
    }

    @Override
    public List<BmsStockBroadCountStockDetailListRspDTO> countStockDetailList(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        List<BmsStockBroadCountStockDetailListRspDTO> result = new ArrayList<>();
        List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
        List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
        List<BmsProductTb> bmsProductTbList = bmsProductTbMapper.selectSelective(null);
        Map<String, BmsProductTb> bmsProductTbMap = bmsProductTbList.stream().collect(Collectors.toMap(BmsProductTb::getProductInnerCode, bmsProductTb -> bmsProductTb));
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
        Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
        Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
        Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectForCountStockDetailList(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsProductStockInLog.class));
        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectForCountStockDetailList(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsProductStockOutLog.class));
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectForCountStockDetailList(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsReturnOrderDetailTb.class));
        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectForCountStockDetailList(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsMoveOrderDetailTb.class));
        //入库数据统计
        if (CollectionUtil.isNotEmpty(bmsProductStockInLogList)) {
            bmsProductStockInLogList.forEach(bmsProductStockInLog -> {
                BmsStockBroadCountStockDetailListRspDTO bmsStockBroadCountStockDetailListRspDTO = new BmsStockBroadCountStockDetailListRspDTO();
                BmsProductTb bmsProductTb = bmsProductTbMap.get(bmsProductStockInLog.getProductInnerCode());
                if (bmsProductTb == null) {
                    throw new BusinessException("数据异常，找不到编号为" + bmsProductStockInLog.getProductInnerCode() + "的商品");
                }
                bmsStockBroadCountStockDetailListRspDTO.setProductInnerCode(bmsProductStockInLog.getProductInnerCode());
                bmsStockBroadCountStockDetailListRspDTO.setProductName(bmsProductTb.getProductName());
                bmsStockBroadCountStockDetailListRspDTO.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                bmsStockBroadCountStockDetailListRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsStockBroadCountStockDetailListRspDTO.getProductCategoryCode()));
                bmsStockBroadCountStockDetailListRspDTO.setBrandCode(bmsProductTb.getBrandCode());
                bmsStockBroadCountStockDetailListRspDTO.setBrandName(bmsBrandMap.get(bmsStockBroadCountStockDetailListRspDTO.getBrandCode()));
                bmsStockBroadCountStockDetailListRspDTO.setProductSpecs(bmsProductTb.getProductSpecs());
                bmsStockBroadCountStockDetailListRspDTO.setBatchNo(bmsProductStockInLog.getBatchNo());
                bmsStockBroadCountStockDetailListRspDTO.setUnitCode(bmsProductStockInLog.getUnitCode());
                bmsStockBroadCountStockDetailListRspDTO.setStockCode(bmsProductStockInLog.getStockCode());
                bmsStockBroadCountStockDetailListRspDTO.setStockName(bmsStockDictMap.get(bmsStockBroadCountStockDetailListRspDTO.getStockCode()));
                bmsStockBroadCountStockDetailListRspDTO.setInAmount(bmsProductStockInLog.getStoreAmount());
                bmsStockBroadCountStockDetailListRspDTO.setInNumber(bmsProductStockInLog.getStoreNumber());
                bmsStockBroadCountStockDetailListRspDTO.setOutAmount(new BigDecimal(0));
                bmsStockBroadCountStockDetailListRspDTO.setOutNumber(0);
                bmsStockBroadCountStockDetailListRspDTO.setReturnAmount(new BigDecimal(0));
                bmsStockBroadCountStockDetailListRspDTO.setReturnNumber(0);
                bmsStockBroadCountStockDetailListRspDTO.setMoveInAmount(new BigDecimal(0));
                bmsStockBroadCountStockDetailListRspDTO.setMoveOutAmount(new BigDecimal(0));
                bmsStockBroadCountStockDetailListRspDTO.setMoveInNumber(0);
                bmsStockBroadCountStockDetailListRspDTO.setMoveOutNumber(0);
                result.add(bmsStockBroadCountStockDetailListRspDTO);
            });
        }
        //出库数据统计
        if (CollectionUtil.isNotEmpty(bmsProductStockOutLogList)) {
            Map<String, BmsStockBroadCountStockDetailListRspDTO> resultMap = result.stream().collect(Collectors.toMap(detail -> detail.getProductInnerCode() + detail.getUnitCode() + detail.getStockCode() + detail.getBatchNo(), detail -> detail));
            bmsProductStockOutLogList.forEach(bmsProductStockOutLog -> {
                BmsProductTb bmsProductTb = bmsProductTbMap.get(bmsProductStockOutLog.getProductInnerCode());
                if (bmsProductTb == null) {
                    throw new BusinessException("数据异常，找不到编号为" + bmsProductStockOutLog.getProductInnerCode() + "的商品");
                }
                BmsStockBroadCountStockDetailListRspDTO bmsStockBroadCountStockDetailListRspDTO = resultMap.get(bmsProductStockOutLog.getProductInnerCode() + bmsProductStockOutLog.getUnitCode() + bmsProductStockOutLog.getStockCode() + bmsProductStockOutLog.getBatchNo());
                if (bmsStockBroadCountStockDetailListRspDTO != null) {
                    bmsStockBroadCountStockDetailListRspDTO.setOutAmount(bmsProductStockOutLog.getOutAmount());
                    bmsStockBroadCountStockDetailListRspDTO.setOutNumber(bmsProductStockOutLog.getOutNumber());
                } else {
                    bmsStockBroadCountStockDetailListRspDTO = new BmsStockBroadCountStockDetailListRspDTO();
                    bmsStockBroadCountStockDetailListRspDTO.setProductName(bmsProductTb.getProductName());
                    bmsStockBroadCountStockDetailListRspDTO.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                    bmsStockBroadCountStockDetailListRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsStockBroadCountStockDetailListRspDTO.getProductCategoryCode()));
                    bmsStockBroadCountStockDetailListRspDTO.setBrandCode(bmsProductTb.getBrandCode());
                    bmsStockBroadCountStockDetailListRspDTO.setBrandName(bmsBrandMap.get(bmsStockBroadCountStockDetailListRspDTO.getBrandCode()));
                    bmsStockBroadCountStockDetailListRspDTO.setProductSpecs(bmsProductTb.getProductSpecs());
                    bmsStockBroadCountStockDetailListRspDTO.setBatchNo(bmsProductStockOutLog.getBatchNo());
                    bmsStockBroadCountStockDetailListRspDTO.setUnitCode(bmsProductStockOutLog.getUnitCode());
                    bmsStockBroadCountStockDetailListRspDTO.setProductInnerCode(bmsProductStockOutLog.getProductInnerCode());
                    bmsStockBroadCountStockDetailListRspDTO.setStockCode(bmsProductStockOutLog.getStockCode());
                    bmsStockBroadCountStockDetailListRspDTO.setStockName(bmsStockDictMap.get(bmsStockBroadCountStockDetailListRspDTO.getStockCode()));
                    bmsStockBroadCountStockDetailListRspDTO.setInAmount(new BigDecimal(0));
                    bmsStockBroadCountStockDetailListRspDTO.setInNumber(0);
                    bmsStockBroadCountStockDetailListRspDTO.setOutAmount(bmsProductStockOutLog.getOutAmount());
                    bmsStockBroadCountStockDetailListRspDTO.setOutNumber(bmsProductStockOutLog.getOutNumber());
                    bmsStockBroadCountStockDetailListRspDTO.setReturnAmount(new BigDecimal(0));
                    bmsStockBroadCountStockDetailListRspDTO.setReturnNumber(0);
                    bmsStockBroadCountStockDetailListRspDTO.setMoveInAmount(new BigDecimal(0));
                    bmsStockBroadCountStockDetailListRspDTO.setMoveOutAmount(new BigDecimal(0));
                    bmsStockBroadCountStockDetailListRspDTO.setMoveInNumber(0);
                    bmsStockBroadCountStockDetailListRspDTO.setMoveOutNumber(0);
                    result.add(bmsStockBroadCountStockDetailListRspDTO);
                }
            });
        }
        //退货数据统计
        if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList)) {
            Map<String, BmsStockBroadCountStockDetailListRspDTO> resultMap = result.stream().collect(Collectors.toMap(detail -> detail.getProductInnerCode() + detail.getUnitCode() + detail.getStockCode() + detail.getBatchNo(), detail -> detail));
            bmsReturnOrderDetailTbList.forEach(bmsReturnOrderDetailTb -> {
                BmsProductTb bmsProductTb = bmsProductTbMap.get(bmsReturnOrderDetailTb.getProductInnerCode());
                if (bmsProductTb == null) {
                    throw new BusinessException("数据异常，找不到编号为" + bmsReturnOrderDetailTb.getProductInnerCode() + "的商品");
                }
                BmsStockBroadCountStockDetailListRspDTO bmsStockBroadCountStockDetailListRspDTO = resultMap.get(bmsReturnOrderDetailTb.getProductInnerCode() + bmsReturnOrderDetailTb.getUnitCode() + bmsReturnOrderDetailTb.getStockCode() + bmsReturnOrderDetailTb.getBatchNo());
                if (bmsStockBroadCountStockDetailListRspDTO != null) {
                    bmsStockBroadCountStockDetailListRspDTO.setReturnNumber(bmsReturnOrderDetailTb.getReturnNumber());
                    bmsStockBroadCountStockDetailListRspDTO.setReturnAmount(bmsReturnOrderDetailTb.getReturnAmount());
                } else {
                    bmsStockBroadCountStockDetailListRspDTO = new BmsStockBroadCountStockDetailListRspDTO();
                    bmsStockBroadCountStockDetailListRspDTO.setProductName(bmsProductTb.getProductName());
                    bmsStockBroadCountStockDetailListRspDTO.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                    bmsStockBroadCountStockDetailListRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsStockBroadCountStockDetailListRspDTO.getProductCategoryCode()));
                    bmsStockBroadCountStockDetailListRspDTO.setBrandCode(bmsProductTb.getBrandCode());
                    bmsStockBroadCountStockDetailListRspDTO.setBrandName(bmsBrandMap.get(bmsStockBroadCountStockDetailListRspDTO.getBrandCode()));
                    bmsStockBroadCountStockDetailListRspDTO.setProductSpecs(bmsProductTb.getProductSpecs());
                    bmsStockBroadCountStockDetailListRspDTO.setBatchNo(bmsReturnOrderDetailTb.getBatchNo());
                    bmsStockBroadCountStockDetailListRspDTO.setUnitCode(bmsReturnOrderDetailTb.getUnitCode());
                    bmsStockBroadCountStockDetailListRspDTO.setProductInnerCode(bmsReturnOrderDetailTb.getProductInnerCode());
                    bmsStockBroadCountStockDetailListRspDTO.setStockCode(bmsReturnOrderDetailTb.getStockCode());
                    bmsStockBroadCountStockDetailListRspDTO.setStockName(bmsStockDictMap.get(bmsStockBroadCountStockDetailListRspDTO.getStockCode()));
                    bmsStockBroadCountStockDetailListRspDTO.setInAmount(new BigDecimal(0));
                    bmsStockBroadCountStockDetailListRspDTO.setInNumber(0);
                    bmsStockBroadCountStockDetailListRspDTO.setReturnNumber(bmsReturnOrderDetailTb.getReturnNumber());
                    bmsStockBroadCountStockDetailListRspDTO.setReturnAmount(bmsReturnOrderDetailTb.getReturnAmount());
                    bmsStockBroadCountStockDetailListRspDTO.setReturnAmount(new BigDecimal(0));
                    bmsStockBroadCountStockDetailListRspDTO.setReturnNumber(0);
                    bmsStockBroadCountStockDetailListRspDTO.setMoveInAmount(new BigDecimal(0));
                    bmsStockBroadCountStockDetailListRspDTO.setMoveOutAmount(new BigDecimal(0));
                    bmsStockBroadCountStockDetailListRspDTO.setMoveInNumber(0);
                    bmsStockBroadCountStockDetailListRspDTO.setMoveOutNumber(0);
                    result.add(bmsStockBroadCountStockDetailListRspDTO);
                }
            });
        }
        //调拨
        if (CollectionUtil.isNotEmpty(bmsMoveOrderDetailTbList)) {
            Map<String, BmsStockBroadCountStockDetailListRspDTO> resultMap = result.stream().collect(Collectors.toMap(detail -> detail.getProductInnerCode() + detail.getUnitCode() + detail.getStockCode() + detail.getBatchNo(), detail -> detail));
            bmsMoveOrderDetailTbList.forEach(bmsMoveOrderDetailTb -> {
                BmsProductTb bmsProductTb = bmsProductTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode());
                if (bmsProductTb == null) {
                    throw new BusinessException("数据异常，找不到编号为" + bmsMoveOrderDetailTb.getProductInnerCode() + "的商品");
                }
                //调出
                BmsStockBroadCountStockDetailListRspDTO outBmsStockBroadCountStockDetailListRspDTO = resultMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getFromStockCode() + bmsMoveOrderDetailTb.getBatchNo());
                outBmsStockBroadCountStockDetailListRspDTO.setMoveOutNumber(bmsMoveOrderDetailTb.getMoveNumber());
                outBmsStockBroadCountStockDetailListRspDTO.setMoveOutAmount(bmsMoveOrderDetailTb.getMoveAmount());
                //调入
                BmsStockBroadCountStockDetailListRspDTO inBmsStockBroadCountStockDetailListRspDTO = resultMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getToStockCode() + bmsMoveOrderDetailTb.getBatchNo());
                if (inBmsStockBroadCountStockDetailListRspDTO != null) {
                    inBmsStockBroadCountStockDetailListRspDTO.setMoveOutNumber(bmsMoveOrderDetailTb.getMoveNumber());
                    inBmsStockBroadCountStockDetailListRspDTO.setMoveOutAmount(bmsMoveOrderDetailTb.getMoveAmount());
                } else {
                    inBmsStockBroadCountStockDetailListRspDTO = new BmsStockBroadCountStockDetailListRspDTO();
                    inBmsStockBroadCountStockDetailListRspDTO.setProductName(bmsProductTb.getProductName());
                    inBmsStockBroadCountStockDetailListRspDTO.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                    inBmsStockBroadCountStockDetailListRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsProductTb.getProductCategoryCode()));
                    inBmsStockBroadCountStockDetailListRspDTO.setBrandCode(bmsProductTb.getBrandCode());
                    inBmsStockBroadCountStockDetailListRspDTO.setBrandName(bmsBrandMap.get(bmsProductTb.getBrandCode()));
                    inBmsStockBroadCountStockDetailListRspDTO.setProductSpecs(bmsProductTb.getProductSpecs());
                    inBmsStockBroadCountStockDetailListRspDTO.setBatchNo(bmsMoveOrderDetailTb.getBatchNo());
                    inBmsStockBroadCountStockDetailListRspDTO.setUnitCode(bmsMoveOrderDetailTb.getUnitCode());
                    inBmsStockBroadCountStockDetailListRspDTO.setProductInnerCode(bmsMoveOrderDetailTb.getProductInnerCode());
                    inBmsStockBroadCountStockDetailListRspDTO.setStockCode(bmsMoveOrderDetailTb.getToStockCode());
                    inBmsStockBroadCountStockDetailListRspDTO.setStockName(bmsStockDictMap.get(inBmsStockBroadCountStockDetailListRspDTO.getStockCode()));
                    inBmsStockBroadCountStockDetailListRspDTO.setInAmount(new BigDecimal(0));
                    inBmsStockBroadCountStockDetailListRspDTO.setInNumber(0);
                    inBmsStockBroadCountStockDetailListRspDTO.setReturnNumber(0);
                    inBmsStockBroadCountStockDetailListRspDTO.setReturnAmount(new BigDecimal(0));
                    inBmsStockBroadCountStockDetailListRspDTO.setReturnAmount(new BigDecimal(0));
                    inBmsStockBroadCountStockDetailListRspDTO.setReturnNumber(0);
                    inBmsStockBroadCountStockDetailListRspDTO.setMoveInAmount(bmsMoveOrderDetailTb.getMoveAmount());
                    inBmsStockBroadCountStockDetailListRspDTO.setMoveOutAmount(new BigDecimal(0));
                    inBmsStockBroadCountStockDetailListRspDTO.setMoveInNumber(bmsMoveOrderDetailTb.getMoveNumber());
                    inBmsStockBroadCountStockDetailListRspDTO.setMoveOutNumber(0);
                    result.add(inBmsStockBroadCountStockDetailListRspDTO);
                }

            });
        }


        return result;
    }

    @Override
    public List<BmsStockBroadCountByCategoryRspDTO> countStockByCategory(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {

        return null;
    }

    @Override
    public List<BmsStockInBroadCountByCategoryRspDTO> countStockInByCategory(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        if (StringUtils.isEmpty(bmsStockBroadCountStockReqDTO.getCountType())) {
            bmsStockBroadCountStockReqDTO.setCountType("month");
            bmsStockBroadCountStockReqDTO.setBeginDateTime("2025-05");
            bmsStockBroadCountStockReqDTO.setEndDateTime(DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN));
        }
        List<BmsStockInBroadCountByCategoryRspDTO> resultList = new ArrayList<>();
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectForCountStockInByCategory(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsProductStockInLog.class));
        if (CollectionUtil.isNotEmpty(bmsProductStockInLogList)) {
            bmsProductStockInLogList.stream().collect(Collectors.groupingBy(BmsProductStockInLog::getDateTime)).forEach((dateTime, list) -> {
                BmsStockInBroadCountByCategoryRspDTO bmsStockInBroadCountByCategoryRspDTO = new BmsStockInBroadCountByCategoryRspDTO();
                bmsStockInBroadCountByCategoryRspDTO.setDateTime(dateTime);
                for (BmsProductCategoryTb bmsProductCategoryTb : bmsProductCategoryTbList) {
                    Map<String, BigDecimal> map = list.stream().collect(Collectors.toMap(BmsProductStockInLog::getProductCategoryCode, BmsProductStockInLog::getStoreAmount));
                    bmsStockInBroadCountByCategoryRspDTO.addContent(bmsProductCategoryTb.getProductCategoryCode(), bmsProductCategoryTb.getProductCategoryName(), map.get(bmsProductCategoryTb.getProductCategoryCode()) == null ? new BigDecimal(0) : map.get(bmsProductCategoryTb.getProductCategoryCode()));
                }
                resultList.add(bmsStockInBroadCountByCategoryRspDTO);
            });
        }
        return resultList.stream().sorted(Comparator.comparing(bmsStockInBroadCountByCategoryRspDTO -> bmsStockInBroadCountByCategoryRspDTO.getDateTime())).collect(Collectors.toList());
    }

    @Override
    public List<BmsStockOutBroadCountByCategoryRspDTO> countStockOutByCategory(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        if (StringUtils.isEmpty(bmsStockBroadCountStockReqDTO.getCountType())) {
            bmsStockBroadCountStockReqDTO.setCountType("month");
            bmsStockBroadCountStockReqDTO.setBeginDateTime("2025-05");
            bmsStockBroadCountStockReqDTO.setEndDateTime(DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN));
        }
        List<BmsStockOutBroadCountByCategoryRspDTO> resultList = new ArrayList<>();
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectForCountStockInByCategory(BeanUtils.copyProperties(bmsStockBroadCountStockReqDTO, BmsProductStockOutLog.class));
        if (CollectionUtil.isNotEmpty(bmsProductStockOutLogList)) {
            bmsProductStockOutLogList.stream().collect(Collectors.groupingBy(BmsProductStockOutLog::getDateTime)).forEach((dateTime, list) -> {
                BmsStockOutBroadCountByCategoryRspDTO bmsStockOutBroadCountByCategoryRspDTO = new BmsStockOutBroadCountByCategoryRspDTO();
                bmsStockOutBroadCountByCategoryRspDTO.setDateTime(dateTime);
                for (BmsProductCategoryTb bmsProductCategoryTb : bmsProductCategoryTbList) {
                    Map<String, BigDecimal> map = list.stream().collect(Collectors.toMap(BmsProductStockOutLog::getProductCategoryCode, BmsProductStockOutLog::getOutAmount));
                    bmsStockOutBroadCountByCategoryRspDTO.addContent(bmsProductCategoryTb.getProductCategoryCode(), bmsProductCategoryTb.getProductCategoryName(), map.get(bmsProductCategoryTb.getProductCategoryCode()) == null ? new BigDecimal(0) : map.get(bmsProductCategoryTb.getProductCategoryCode()));
                }
                resultList.add(bmsStockOutBroadCountByCategoryRspDTO);
            });
        }
        return resultList.stream().sorted(Comparator.comparing(bmsStockOutBroadCountByCategoryRspDTO -> bmsStockOutBroadCountByCategoryRspDTO.getDateTime())).collect(Collectors.toList());

    }
}
