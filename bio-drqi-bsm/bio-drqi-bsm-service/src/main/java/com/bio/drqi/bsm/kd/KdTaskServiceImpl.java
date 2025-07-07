package com.bio.drqi.bsm.kd;

import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.kd.dto.QuerySupplierDTO;
import com.bio.drqi.bsm.kd.enums.OperateEnum;
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
    private BmsStockLocationDictMapper bmsStockLocationDictMapper;

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
        bmsProjectDictList.forEach(bmsProjectDict -> {
            if (bmsProjectDict.getKdNumber() == null) {
                kdApiService.execute(OperateEnum.projectSave, bmsProjectDict, "beijing");
            } else {
                kdApiService.execute(OperateEnum.projectModify, bmsProjectDict, "beijing");
            }

        });
        log.info("*****************项目同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);

    }

    @Override
    public void synBrandTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************品牌同步开始**************************");
        List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectList(null);
        bmsBrandTbList.forEach(bmsBrandTb -> {
            log.info("正在同步品牌{}", bmsBrandTb.getBrandName());
            if (BioBsmContents.N.equals(bmsBrandTb.getDeleteFlag())) {
                if (bmsBrandTb.getKdNumber() > 0) {
                    kdApiService.execute(OperateEnum.bmsModify, bmsBrandTb, "beijing");
                } else {
                    String kdNumber = kdApiService.execute(OperateEnum.bmsSave, bmsBrandTb, "beijing");
                    bmsBrandTb.setKdNumber(Integer.valueOf(kdNumber));
                }
            } else {
                kdApiService.execute(OperateEnum.bmsDisable, bmsBrandTb, "beijing");
            }
        });
        log.info("*****************品牌同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);
    }

    @Override
    public void synStockTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************仓库同步开始**************************");
        List<BmsStockLocationDict> bmsStockLocationDictList = bmsStockLocationDictMapper.selectList(null);
        Map<String, List<BmsStockLocationDict>> listMap = bmsStockLocationDictList.stream().collect(Collectors.groupingBy(BmsStockLocationDict::getStockCode));
        listMap.forEach((stockCode, list) -> {
            kdApiService.execute(OperateEnum.stockSave, list.get(0), list.get(0).getUnitCode());
        });
        log.info("*****************仓库同步结束，耗时={}ms**************************", System.currentTimeMillis() - startTime);


    }

    @Override
    public void synMaterialGroupTask() {
        Long startTime = System.currentTimeMillis();
        log.info("*****************分组同步开始**************************");
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectList(null);
        for (BmsProductCategoryTb bmsProductCategoryTb : bmsProductCategoryTbList) {
            if(bmsProductCategoryTb.getKdNumber()>0){
                String idStr = kdApiService.execute(OperateEnum.groupSave, bmsProductCategoryTb, "beijing");
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
