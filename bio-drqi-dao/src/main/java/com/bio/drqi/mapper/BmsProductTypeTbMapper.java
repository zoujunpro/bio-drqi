package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsProductTypeTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bms_product_type_tb(商品类型ID)】的数据库操作Mapper
* @createDate 2025-02-27 10:16:10
* @Entity com.bio.drqi.domain.BmsProductTypeTb
*/
public interface BmsProductTypeTbMapper extends BaseMapper<BmsProductTypeTb> {

    List<BmsProductTypeTb> selectSelective(BmsProductTypeTb bmsProductTypeTb);


}




