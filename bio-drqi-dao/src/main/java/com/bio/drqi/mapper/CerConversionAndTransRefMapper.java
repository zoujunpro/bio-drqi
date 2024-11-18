package com.bio.drqi.mapper;
import java.util.List;

import com.bio.drqi.domain.CerConversionAndTransRef;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_conversion_and_trans_ref(转化疫苗取样编号关联表)】的数据库操作Mapper
* @createDate 2024-08-06 10:36:57
* @Entity com.bio.cer.domain.CerConversionAndTransRef
*/
public interface CerConversionAndTransRefMapper extends BaseMapper<CerConversionAndTransRef> {
    List<CerConversionAndTransRef> selectAllByConversionAndTransId(@Param("conversionAndTransId") Integer conversionAndTransId);

    List<CerConversionAndTransRef> selectAllByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);

    int deleteByConversionAndTransId(@Param("conversionAndTransId") Integer conversionAndTransId);


    List<CerConversionAndTransRef> selectSelective(CerConversionAndTransRef cerConversionAndTransRef);

}




