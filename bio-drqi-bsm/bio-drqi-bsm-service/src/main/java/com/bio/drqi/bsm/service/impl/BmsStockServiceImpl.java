package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.dto.BmsCountPeriodTaskDTO;
import com.bio.drqi.bsm.dto.BmsJieCunStockExcelDTO;
import com.bio.drqi.bsm.kd.KdTaskExecuteService;
import com.bio.drqi.bsm.req.BmsStockAddReqDTO;
import com.bio.drqi.bsm.req.BmsStockEditReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockQueryByUnitRspDTO;
import com.bio.drqi.bsm.service.BmsStockService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsStockServiceImpl implements BmsStockService {

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private KdTaskExecuteService kdTaskExecuteService;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Override
    public List<BmsStockQueryByUnitRspDTO> queryStockByUnit(String unitCode) {
        List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectAllByUnitCodeOrderByIdDesc(unitCode);
        return BeanUtils.copyListProperties(bmsStockDictList, BmsStockQueryByUnitRspDTO.class);
    }

    @Override
    public void add(BmsStockAddReqDTO bmsStockAddReqDTO) {
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockNameAndUnitCode(bmsStockAddReqDTO.getStockName(), bmsStockAddReqDTO.getUnitCode());
        if (bmsStockDict != null) {
            throw new BusinessException("已经有此库房");
        }
        bmsStockDict = new BmsStockDict();
        bmsStockDict.setStockName(bmsStockAddReqDTO.getStockName());
        bmsStockDict.setStockCode(IdUtils.simpleUUID().substring(0, 30));
        bmsStockDict.setUnitCode(bmsStockAddReqDTO.getUnitCode());
        bmsStockDict.setKdNumber(null);
        bmsStockDict.setCreateTime(new Date());
        bmsStockDict.setCreateUserId(SecurityContextHolder.getUserId());
        bmsStockDict.setCreateUserName(SecurityContextHolder.getNickName());
        try {
            bmsStockDictMapper.insert(bmsStockDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("已经存在此名字库房");
        }

    }

    @Override
    public void edit(BmsStockEditReqDTO bmsStockEditReqDTO) {
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectById(bmsStockEditReqDTO.getId());
        if (bmsStockDict == null) {
            throw new BusinessException("不存在此库房");
        }
        bmsStockDict.setStockName(bmsStockEditReqDTO.getStockName());
        try {
            bmsStockDictMapper.updateById(bmsStockDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("已经存在此名字库房");
        }

    }

    @Override
    public void delete(Integer id) {
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectById(id);
        List<BmsProductStockTb> list = bmsProductStockTbMapper.selectAllByStockCode(bmsStockDict.getStockCode());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BusinessException("该库房已经使用，无法删除");
        }
        bmsStockDictMapper.deleteById(id);

    }

    @Override
    public void downJieCunStockExcel(String dateTime, HttpServletResponse httpServletResponse) {
        String beginDate = DateUtil.format(DateUtil.offsetDay(DateUtil.parse(dateTime, DatePattern.NORM_DATE_PATTERN), 1), DatePattern.NORM_DATE_PATTERN);
        //step 数据查询
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        Map<String, BmsProductStockTb> bmsProductStockTbMap = bmsProductStockTbList.stream().collect(Collectors.toMap(bmsProductStockTb -> bmsProductStockTb.getProductInnerCode() + bmsProductStockTb.getUnitCode() + bmsProductStockTb.getBatchNo() + bmsProductStockTb.getStockCode(), bmsProductStockTb -> bmsProductStockTb));

        List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
        List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
        Map<String, String> bmsSupplierTbMap = bmsSupplierTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName));
        Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
        Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
        Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));

        //查询需要回退的数据

        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BmsProductStockInLog.builder().startDate(beginDate).build());
        log.info("bmsProductStockInLogList :" + bmsProductStockInLogList.size());

        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(BmsProductStockOutLog.builder().startDate(beginDate).build());
        log.info("bmsProductStockOutLogList :" + bmsProductStockOutLogList.size());


        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectSelective(BmsMoveOrderDetailTb.builder().startDate(beginDate).build());
        log.info("bmsMoveOrderDetailTbList :" + bmsMoveOrderDetailTbList.size());


        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectSelective(BmsReturnOrderDetailTb.builder().startDate(beginDate).build());
        log.info("bmsReturnOrderDetailTbList :" + bmsReturnOrderDetailTbList.size());

        //复原库存
        //先复原出库  出库的数据加到库存中
        for (BmsProductStockOutLog bmsProductStockOutLog : bmsProductStockOutLogList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsProductStockOutLog.getProductInnerCode() + bmsProductStockOutLog.getUnitCode() + bmsProductStockOutLog.getBatchNo() + bmsProductStockOutLog.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockOutLog.getOutNumber().add(bmsProductStockTb.getCurrentStockNumber()));
        }
        //复原退货
        for (BmsReturnOrderDetailTb bmsReturnOrderDetailTb : bmsReturnOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsReturnOrderDetailTb.getProductInnerCode() + bmsReturnOrderDetailTb.getUnitCode() + bmsReturnOrderDetailTb.getBatchNo() + bmsReturnOrderDetailTb.getStockCode());
            log.info("bmsReturnOrderDetailTb={}" + JSONUtil.toJsonStr(bmsReturnOrderDetailTb));
            bmsProductStockTb.setCurrentStockNumber(bmsReturnOrderDetailTb.getReturnNumber().add(bmsProductStockTb.getCurrentStockNumber()));
        }
        //复原调拨
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb : bmsMoveOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getBatchNo() + bmsMoveOrderDetailTb.getFromStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsMoveOrderDetailTb.getMoveNumber().add(bmsProductStockTb.getCurrentStockNumber()));
        }
        //回退入库的
        for (BmsProductStockInLog bmsProductStockInLog : bmsProductStockInLogList) {
            log.info("bmsProductStockInLog=" + JSONUtil.toJsonStr(bmsProductStockInLog));
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsProductStockInLog.getProductInnerCode() + bmsProductStockInLog.getUnitCode() + bmsProductStockInLog.getBatchNo().trim() + bmsProductStockInLog.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber().subtract(bmsProductStockInLog.getStoreNumber()));
        }
        //回退调拨的
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb : bmsMoveOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getBatchNo() + bmsMoveOrderDetailTb.getToStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber().subtract(bmsMoveOrderDetailTb.getMoveNumber()));
        }
        List<BmsJieCunStockExcelDTO> bmsStockList = BeanUtils.copyListProperties(bmsProductStockTbList, BmsJieCunStockExcelDTO.class);
        bmsStockList = bmsStockList.stream().filter(bmsStock -> bmsStock.getCurrentStockNumber() > 0).collect(Collectors.toList());
        for (BmsJieCunStockExcelDTO bmsStock : bmsStockList) {
            List<BmsProductStockInLog> bmsProductStockInLogs = bmsProductStockInLogMapper.selectAllByUniqueCode(bmsStock.getUniqueCode());
            if (CollectionUtil.isNotEmpty(bmsProductStockInLogs)) {
                String projectCode = bmsProductStockInLogs.get(0).getProjectCode();
                BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectOneByProjectCode(projectCode);
                bmsStock.setProjectCode(bmsProjectDict.getProjectCode());
                bmsStock.setProjectType(bmsProjectDict.getKdProjectType());
                bmsStock.setProductName(bmsProjectDict.getKdProjectName());
            }
            bmsStock.setStockName(bmsStockDictMap.get(bmsStock.getStockCode()));
            bmsStock.setProductCategoryName(bmsProductCategoryTbMap.get(bmsStock.getProductCategoryCode()));
            bmsStock.setBrandName(bmsBrandMap.get(bmsStock.getBrandCode()));
            bmsStock.setSupplierName(bmsSupplierTbMap.get(bmsStock.getSupplierCode()));
        }

        ExcelUtil.writeExcel("D://" + dateTime, "sheet1", bmsStockList, BmsJieCunStockExcelDTO.class, httpServletResponse);
    }

}
