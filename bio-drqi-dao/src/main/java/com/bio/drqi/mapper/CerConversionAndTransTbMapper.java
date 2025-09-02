package com.bio.drqi.mapper;

import com.bio.drqi.domain.CerConversionAndTransTb;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【cer_conversion_and_trans_tb(转化疫苗记录表)】的数据库操作Mapper
 * @createDate 2024-08-07 18:14:44
 * @Entity com.bio.cer.domain.CerConversionAndTransTb
 */
public interface CerConversionAndTransTbMapper extends BaseMapper<CerConversionAndTransTb> {

    List<CerConversionAndTransTb> selectAllOrderByIdDesc();

    List<CerConversionAndTransTb> selectSelective(CerConversionAndTransTb cerConversionAndTransTb);
    CerConversionAndTransTb selectOneByTaskNum(@Param("taskNum") String taskNum);

    int deleteByTaskNum(@Param("taskNum") String taskNum);

    Integer selectCountNum();

    List<CerConversionAndTransTb> selectCountTransNumByMonth(@Param("year") String year);


}




