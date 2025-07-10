package com.bio.drqi.bsm.kd;

import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KdTaskServiceImpl implements KdTaskService {

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

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

    public void synProjectTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************项目同步开始**************************");
        List<BmsProjectDict> bmsProjectDictList = bmsProjectDictMapper.selectList(null);
        Map<String, List<BmsProjectDict>> mapList = bmsProjectDictList.stream().collect(Collectors.groupingBy(BmsProjectDict::getKdProjectCode));
        mapList.forEach((kdProjectCode, list) -> {
            if(StringUtils.isNotEmpty(kdProjectCode)){
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

    /**
     * 不用实现
     */
    @Override
    @Deprecated
    public void synBrandTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************品牌同步开始**************************");
     /*   List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectList(null);
        bmsBrandTbList.forEach(bmsBrandTb -> {
            log.info("正在同步品牌{}", bmsBrandTb.getBrandName());
            if (BioBsmContents.N.equals(bmsBrandTb.getDeleteFlag())) {
                if (bmsBrandTb.getKdNumber() != null && bmsBrandTb.getKdNumber() > 0) {
                    kdApiService.execute(OperateEnum.bmsModify, bmsBrandTb, PurchaseUnitEnum.default_.name());
                } else {
                    String kdNumber = kdApiService.execute(OperateEnum.bmsSave, bmsBrandTb, PurchaseUnitEnum.default_.name());
                    bmsBrandTb.setKdNumber(Integer.valueOf(kdNumber));
                    bmsBrandTbMapper.updateById(bmsBrandTb);
                }
            } else {
                kdApiService.execute(OperateEnum.bmsDisable, bmsBrandTb, "beijing");
            }
        });*/
        log.info("*****************品牌同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);
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
            if (bmsProductCategoryTb.getKdNumber() != null && bmsProductCategoryTb.getKdNumber() > 0) {
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
        List<QuerySupplierDTO> querySupplierDTOList = KdRequestUtil.executeQuerySupplier();
        for (QuerySupplierDTO querySupplierDTO : querySupplierDTOList) {
            BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierName(querySupplierDTO.getFName());
            if (bmsSupplierTb != null) {
                bmsSupplierTb.setKdNumber(Integer.valueOf(querySupplierDTO.getFNumber()));
                bmsSupplierTbMapper.updateById(bmsSupplierTb);
            }

        }
        log.info("*****************供应商同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);


    }

    @Override
    public void synMaterialTask() {

    }

    @Override
    public void synInStockTask() {

    }

    @Override
    public void synOutStockTask() {

    }
}
