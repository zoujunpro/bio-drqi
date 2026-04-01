package com.bio.drqi.mapper;
import java.util.Collection;

import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsSupplierTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author zou'jun
 * @description 针对表【bms_supplier_tb(供应商信息表)】的数据库操作Mapper
 * @createDate 2025-02-27 10:16:10
 * @Entity com.bio.drqi.domain.BmsSupplierTb
 */
public interface BmsSupplierTbMapper extends BaseMapper<BmsSupplierTb> {

    List<BmsSupplierTb> selectSelective(BmsSupplierTb bmsSupplierTb);

    List<BmsSupplierTb> selectSupplierCodeAndSupplierCodeBySupplierStatusOrderByIdDesc(@Param("supplierStatus") String supplierStatus);

    BmsSupplierTb selectOneBySupplierCode(@Param("supplierCode") String supplierCode);

    BmsSupplierTb selectOneBySupplierName(@Param("supplierName") String supplierName);

    List<BmsSupplierTb> selectAllBySupplierCodeIn(@Param("supplierCodeList") Collection<String> supplierCodeList);

    String selectMaxSupplierCode();

}




