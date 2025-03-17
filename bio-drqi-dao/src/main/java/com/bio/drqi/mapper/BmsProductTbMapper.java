package com.bio.drqi.mapper;

import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsProductTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author zou'jun
 * @description 针对表【bms_product_tb(商品信息表)】的数据库操作Mapper
 * @createDate 2025-02-27 10:16:10
 * @Entity com.bio.drqi.domain.BmsProductTb
 */
public interface BmsProductTbMapper extends BaseMapper<BmsProductTb> {
    int updateDeleteFlagBySupplierCode(@Param("deleteFlag") String deleteFlag, @Param("supplierCode") String supplierCode);

    int updateDeleteFlagByBrandCode(@Param("deleteFlag") String deleteFlag, @Param("brandCode") String brandCode);


    List<BmsProductTb> selectAllOrderByIdDesc();

    List<String> selectProductNameOrderByIdDesc();

    List<BmsProductTb> selectSelective(BmsProductTb bmsProductTb);

    List<BmsProductTb> selectAllByProductTypeCode(@Param("productTypeCode") String productTypeCode);

    List<BmsProductTb> selectAllByProductCategoryCode(@Param("productCategoryCode") String productCategoryCode);


    List<BmsProductTb> selectAllByProductNameAndBrandCode(@Param("productName") String productName, @Param("brandCode") String brandCode);

    BmsProductTb selectOneByBrandCodeAndProductNameAndProductSpecs(@Param("brandCode") String brandCode, @Param("productName") String productName, @Param("productSpecs") String productSpecs);
}




