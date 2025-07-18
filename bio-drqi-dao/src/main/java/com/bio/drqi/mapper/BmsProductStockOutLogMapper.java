package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsProductStockOutLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bms_product_stock_out_log(出库记录日志表)】的数据库操作Mapper
* @createDate 2025-03-21 14:59:06
* @Entity com.bio.drqi.domain.BmsProductStockOutLog
*/
public interface BmsProductStockOutLogMapper extends BaseMapper<BmsProductStockOutLog> {



    List<BmsProductStockOutLog> selectSelective(BmsProductStockOutLog bmsProductStockOutLog);

    List<BmsProductStockOutLog> selectAllByUniqueCode(@Param("uniqueCode") String uniqueCode);
}




