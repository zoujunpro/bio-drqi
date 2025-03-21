package com.bio.drqi.mapper;

import com.bio.drqi.domain.BmsProductStockInLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bms_product_stock_in_log(入库记录日志表)】的数据库操作Mapper
* @createDate 2025-03-21 14:55:03
* @Entity com.bio.drqi.domain.BmsProductStockInLog
*/
public interface BmsProductStockInLogMapper extends BaseMapper<BmsProductStockInLog> {
List<BmsProductStockInLog> selectSelective(BmsProductStockInLog bmsProductStockInLog);
}




