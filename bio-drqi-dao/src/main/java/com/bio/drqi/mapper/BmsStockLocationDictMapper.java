package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsStockLocationDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bms_stock_location_dict(库位字典表)】的数据库操作Mapper
* @createDate 2025-03-17 15:14:29
* @Entity com.bio.drqi.domain.BmsStockLocationDict
*/
public interface BmsStockLocationDictMapper extends BaseMapper<BmsStockLocationDict> {
    List<BmsStockLocationDict> selectAllByUnitCode(@Param("unitCode") String unitCode);

    BmsStockLocationDict selectOneByUnitCodeAndLocaltionNumber(@Param("unitCode") String unitCode, @Param("localtionNumber") String localtionNumber);

}




