package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BioSampleCodePrefixTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bio_sample_code_prefix_tb(取样编号前缀表)】的数据库操作Mapper
* @createDate 2024-12-18 14:18:31
* @Entity com.bio.drqi.domain.CerSampleCodePrefixTb
*/
public interface BioSampleCodePrefixTbMapper extends BaseMapper<BioSampleCodePrefixTb> {
    BioSampleCodePrefixTb selectOneByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);

    BioSampleCodePrefixTb selectOneByTaskNum(@Param("taskNum") String taskNum);

    int deleteByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);
}




