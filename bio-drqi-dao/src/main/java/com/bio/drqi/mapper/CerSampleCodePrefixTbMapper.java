package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerSampleCodePrefixTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sample_code_prefix_tb(取样编号前缀表)】的数据库操作Mapper
* @createDate 2024-12-18 14:18:31
* @Entity com.bio.drqi.domain.CerSampleCodePrefixTb
*/
public interface CerSampleCodePrefixTbMapper extends BaseMapper<CerSampleCodePrefixTb> {
    CerSampleCodePrefixTb selectOneByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);

    int deleteByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);
}




