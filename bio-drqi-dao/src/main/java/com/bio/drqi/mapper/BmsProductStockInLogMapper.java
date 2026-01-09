package com.bio.drqi.mapper;
import java.util.Date;
import com.bio.drqi.domain.BmsProductStockOutLog;
import com.bio.drqi.domain.BmsProductStockTb;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsProductStockInLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;
import java.util.List;

/**
* @author zou'jun
* @description 针对表【bms_product_stock_in_log(入库记录日志表)】的数据库操作Mapper
* @createDate 2025-03-21 14:55:03
* @Entity com.bio.drqi.domain.BmsProductStockInLog
*/
public interface BmsProductStockInLogMapper extends BaseMapper<BmsProductStockInLog> {
List<BmsProductStockInLog> selectSelective(BmsProductStockInLog bmsProductStockInLog);

    List<BmsProductStockInLog> selectAllByTaskNum(@Param("taskNum") String taskNum);

    List<BmsProductStockInLog> selectAllByOrderDetailNum(@Param("orderDetailNum") String orderDetailNum);
    List<BmsProductStockInLog> selectAllByUniqueCode(@Param("uniqueCode") String uniqueCode);

    BmsProductStockInLog selectOneByTaskNumAndProductInnerCodeAndBatchNo(@Param("taskNum") String taskNum, @Param("productInnerCode") String productInnerCode, @Param("batchNo") String batchNo);

    BigDecimal selectSumAmount(BmsProductStockInLog bmsProductStockInLog);

    List<BmsProductStockInLog> selectForCountStockDetailList(BmsProductStockInLog bmsProductStockInLog);

    List<BmsProductStockInLog> selectForCountStockInByCategory(BmsProductStockInLog bmsProductStockInLog);

    List<BmsProductStockInLog> selectAllByCreateTime(@Param("month") String month);


}




