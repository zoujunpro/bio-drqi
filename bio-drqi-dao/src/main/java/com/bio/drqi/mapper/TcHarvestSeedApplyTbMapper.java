package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.TcHarvestSeedApplyTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【tc_harvest_seed_apply_tb】的数据库操作Mapper
* @createDate 2025-05-16 13:24:41
* @Entity com.bio.drqi.domain.TcHarvestSeedApplyTb
*/
public interface TcHarvestSeedApplyTbMapper extends BaseMapper<TcHarvestSeedApplyTb> {

    TcHarvestSeedApplyTb selectOneByPollinationApplyNum(@Param("pollinationApplyNum") String pollinationApplyNum);

}




