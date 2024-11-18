package com.bio.drqi.mapper;
import com.bio.drqi.domain.SeedQualityCheckDtlTb;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【seed_quality_check_dtl_tb】的数据库操作Mapper
* @createDate 2024-06-19 09:42:58
* @Entity com.bio.cer.domain.SeedQualityCheckDtlTb
*/
public interface SeedQualityCheckDtlTbMapper extends BaseMapper<SeedQualityCheckDtlTb> {

    List<SeedQualityCheckDtlTb> selectSelective(SeedQualityCheckDtlTb seedQualityCheckDtlTb);

}




