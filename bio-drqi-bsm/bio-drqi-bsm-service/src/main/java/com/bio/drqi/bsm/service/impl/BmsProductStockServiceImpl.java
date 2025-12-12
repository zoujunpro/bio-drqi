package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.req.BmsProductStockEditDateReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockMoveStockReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockQueryListReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsProductStockServiceImpl implements BmsProductStockService {

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;


    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Override
    public PageInfo<BmsProductStockListPageRspDTO> listPage(BmsProductStockListPageReqDTO bmsProductStockListPageReqDTO) {
        PageHelper.startPage(bmsProductStockListPageReqDTO.getPageNum(), bmsProductStockListPageReqDTO.getPageSize());
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockListPageReqDTO, BmsProductStockTb.class));
        PageInfo<BmsProductStockTb> srcPageInfo = new PageInfo<>(bmsProductStockTbList);
        PageInfo<BmsProductStockListPageRspDTO> targetPage = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductStockListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPage.getList())) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsSupplierTbMap = bmsSupplierTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName));
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            targetPage.getList().forEach(bmsProductStockListPageRspDTO -> {
                bmsProductStockListPageRspDTO.setStockName(bmsStockDictMap.get(bmsProductStockListPageRspDTO.getStockCode()));
                bmsProductStockListPageRspDTO.setBrandName(bmsBrandMap.get(bmsProductStockListPageRspDTO.getBrandCode()));
                bmsProductStockListPageRspDTO.setSupplierName(bmsSupplierTbMap.get(bmsProductStockListPageRspDTO.getSupplierCode()));
                bmsProductStockListPageRspDTO.setProductCategoryCode(bmsProductCategoryTbMap.get(bmsProductStockListPageRspDTO.getProductCategoryCode()));
            });
        }
        return targetPage;
    }

    @Override
    public BmsProductStockDetailRspDTO detail(Integer id) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectById(id);
        if (bmsProductStockTb == null) {
            throw new BusinessException("数据找不到");
        }
        BmsProductStockDetailRspDTO bmsProductStockDetailRspDTO = BeanUtils.copyProperties(bmsProductStockTb, BmsProductStockDetailRspDTO.class);
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsProductStockDetailRspDTO.getStockCode());
        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(bmsProductStockTb.getProductCategoryCode());
        BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(bmsProductStockTb.getSupplierCode());
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductStockTb.getBrandCode());
        bmsProductStockDetailRspDTO.setStockName(bmsStockDict != null ? bmsStockDict.getStockName() : null);
        bmsProductStockDetailRspDTO.setProductCategoryName(bmsProductCategoryTb == null ? null : bmsProductCategoryTb.getProductCategoryName());
        bmsProductStockDetailRspDTO.setSupplierName(bmsSupplierTb == null ? null : bmsSupplierTb.getSupplierName());
        bmsProductStockDetailRspDTO.setBrandName(bmsBrandTb==null?null:bmsBrandTb.getBrandName());
        return bmsProductStockDetailRspDTO;
    }

    @Override
    public List<String> queryStockByUnitCode(String unitCode) {
        List<String> productNameList = bmsProductStockTbMapper.selectProductNameByUnitCode(unitCode);
        if (CollectionUtil.isNotEmpty(productNameList)) {
            return productNameList.stream().distinct().collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<BmsProductStockQueryListRspDTO> queryList(BmsProductStockQueryListReqDTO bmsProductStockQueryListReqDTO) {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockQueryListReqDTO, BmsProductStockTb.class));
        List<BmsProductStockQueryListRspDTO> bmsProductStockQueryListRspDTOList = BeanUtils.copyListProperties(bmsProductStockTbList, BmsProductStockQueryListRspDTO.class);
        if (CollectionUtil.isNotEmpty(bmsProductStockQueryListRspDTOList)) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            bmsProductStockQueryListRspDTOList.forEach(bmsProductStockQueryListRspDTO -> {
                bmsProductStockQueryListRspDTO.setStockName(bmsStockDictMap.get(bmsProductStockQueryListRspDTO.getStockCode()));
            });
        }
        return bmsProductStockQueryListRspDTOList;
    }

    @Override
    public void editDate(BmsProductStockEditDateReqDTO bmsProductStockEditDateReqDTO) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectById(bmsProductStockEditDateReqDTO.getId());
        if (bmsProductStockTb == null) {
            throw new BusinessException("库存中不存在此耗材");
        }
        bmsProductStockTb.setExpirationDate(bmsProductStockEditDateReqDTO.getExpirationDate());
        bmsProductStockTb.setProduceDate(bmsProductStockEditDateReqDTO.getProduceDate());
        bmsProductStockTbMapper.updateById(bmsProductStockTb);

    }

    @Override
    public void moveStock(BmsProductStockMoveStockReqDTO bmsProductStockMoveStockReqDTO) {
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsProductStockMoveStockReqDTO.getNewStockCode());
        if (bmsStockDict == null) {
            throw new BusinessException("库房不存在");
        }
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectById(bmsProductStockMoveStockReqDTO.getId());
        if (bmsProductStockTb == null) {
            throw new BusinessException("库存中不存在此耗材");
        }
        if (bmsProductStockTb.getCurrentStockNumber() < bmsProductStockMoveStockReqDTO.getMoveNumber()) {
            throw new BusinessException("当前库存数量不足");
        }
        if (bmsProductStockTb.getStockCode().equals(bmsProductStockMoveStockReqDTO.getNewStockCode())) {
            throw new BusinessException("库房无变化");
        }
        if (!bmsStockDict.getUnitCode().equals(bmsProductStockTb.getUnitCode())) {
            throw new BusinessException("不能跨单位调拨");
        }
        // 移除库存扣减
        bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber() - bmsProductStockMoveStockReqDTO.getMoveNumber());
        bmsProductStockTb.setTotalStoreNumber(bmsProductStockTb.getTotalStoreNumber() - bmsProductStockMoveStockReqDTO.getMoveNumber());
        bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(bmsProductStockMoveStockReqDTO.getOldStockLocationList()));
        bmsProductStockTbMapper.updateById(bmsProductStockTb);

        //移入库存添加
        BmsProductStockTb newBmsProductStockTb = bmsProductStockTbMapper.selectOneByProductInnerCodeAndUnitCodeAndBatchNoAndStockCode(bmsProductStockTb.getProductInnerCode(), bmsProductStockTb.getUnitCode(), bmsProductStockTb.getBatchNo(), bmsProductStockMoveStockReqDTO.getNewStockCode());
        if (newBmsProductStockTb != null) {
            newBmsProductStockTb.setCurrentStockNumber(newBmsProductStockTb.getCurrentStockNumber() + bmsProductStockMoveStockReqDTO.getMoveNumber());
            newBmsProductStockTb.setTotalStoreNumber(newBmsProductStockTb.getTotalStoreNumber() + bmsProductStockMoveStockReqDTO.getMoveNumber());
            List<String> currentStockLoationList = JSONUtil.toList(newBmsProductStockTb.getStockLocationNumber(), String.class);
            currentStockLoationList.addAll(bmsProductStockMoveStockReqDTO.getNewStockLocationList());
            newBmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(currentStockLoationList.stream().distinct().collect(Collectors.toList())));
            bmsProductStockTbMapper.updateById(newBmsProductStockTb);
        } else {
            newBmsProductStockTb = new BmsProductStockTb();
            newBmsProductStockTb.setProductName(bmsProductStockTb.getProductName());
            newBmsProductStockTb.setProductOutCode(bmsProductStockTb.getProductOutCode());
            newBmsProductStockTb.setProductCategoryCode(bmsProductStockTb.getProductCategoryCode());
            newBmsProductStockTb.setBrandCode(bmsProductStockTb.getBrandCode());
            newBmsProductStockTb.setProductSpecs(bmsProductStockTb.getProductSpecs());
            newBmsProductStockTb.setBatchNo(bmsProductStockTb.getBatchNo());
            newBmsProductStockTb.setTotalStoreNumber(bmsProductStockMoveStockReqDTO.getMoveNumber());
            newBmsProductStockTb.setCurrentStockNumber(bmsProductStockMoveStockReqDTO.getMoveNumber());
            newBmsProductStockTb.setTotalOutNumber(0);
            newBmsProductStockTb.setUnitCode(bmsProductStockTb.getUnitCode());
            newBmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(bmsProductStockMoveStockReqDTO.getNewStockLocationList()));
            newBmsProductStockTb.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
            newBmsProductStockTb.setSupplierCode(bmsProductStockTb.getSupplierCode());
            newBmsProductStockTb.setUniqueCode(IdUtils.simpleUUID());
            newBmsProductStockTb.setProduceDate(bmsProductStockTb.getProduceDate());
            newBmsProductStockTb.setExpirationDate(bmsProductStockTb.getExpirationDate());
            newBmsProductStockTb.setReturnNumber(0);
            newBmsProductStockTb.setStockCode(bmsProductStockMoveStockReqDTO.getNewStockCode());
            bmsProductStockTbMapper.insert(newBmsProductStockTb);
        }
        //记录移库流水
        BmsMoveOrderDetailTb bmsMoveOrderDetailTb = new BmsMoveOrderDetailTb();
        bmsMoveOrderDetailTb.setProductName(newBmsProductStockTb.getProductName());
        bmsMoveOrderDetailTb.setProductOutCode(newBmsProductStockTb.getProductOutCode());
        bmsMoveOrderDetailTb.setProductCategoryCode(newBmsProductStockTb.getProductCategoryCode());
        bmsMoveOrderDetailTb.setBrandCode(newBmsProductStockTb.getBrandCode());
        bmsMoveOrderDetailTb.setProductSpecs(newBmsProductStockTb.getProductSpecs());
        bmsMoveOrderDetailTb.setBatchNo(newBmsProductStockTb.getBatchNo());
        bmsMoveOrderDetailTb.setUnitCode(newBmsProductStockTb.getUnitCode());
        bmsMoveOrderDetailTb.setSupplierCode(newBmsProductStockTb.getSupplierCode());
        bmsMoveOrderDetailTb.setProductInnerCode(newBmsProductStockTb.getProductInnerCode());
        bmsMoveOrderDetailTb.setProduceDate(newBmsProductStockTb.getProduceDate());
        bmsMoveOrderDetailTb.setExpirationDate(newBmsProductStockTb.getExpirationDate());
        bmsMoveOrderDetailTb.setFromStockCode(bmsProductStockTb.getStockCode());
        bmsMoveOrderDetailTb.setFromStockLocationNumber(bmsProductStockTb.getStockLocationNumber());
        bmsMoveOrderDetailTb.setToStockCode(newBmsProductStockTb.getStockCode());
        bmsMoveOrderDetailTb.setToStockLocationNumber(newBmsProductStockTb.getStockLocationNumber());
        bmsMoveOrderDetailTb.setMoveNumber(bmsProductStockMoveStockReqDTO.getMoveNumber());
        bmsMoveOrderDetailTb.setCreateUserId(SecurityContextHolder.getUserId());
        bmsMoveOrderDetailTb.setCreateUserName(SecurityContextHolder.getNickName());
        bmsMoveOrderDetailTb.setCreateTime(new Date());
        bmsMoveOrderDetailTb.setUniqueCode(newBmsProductStockTb.getUniqueCode());
        bmsMoveOrderDetailTbMapper.insert(bmsMoveOrderDetailTb);
    }
}
