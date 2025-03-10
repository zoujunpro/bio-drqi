package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsProductCategoryTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【bms_product_category_tb(商品类别表)】的数据库操作Mapper
* @createDate 2025-02-27 10:16:09
* @Entity com.bio.drqi.domain.BmsProductCategoryTb
*/
public interface BmsProductCategoryTbMapper extends BaseMapper<BmsProductCategoryTb> {

    BmsProductCategoryTb selectOneByProductCategoryName(@Param("productCategoryName") String productCategoryName);

    List<BmsProductCategoryTb> selectSelective(BmsProductCategoryTb bmsProductCategoryTb);

}




