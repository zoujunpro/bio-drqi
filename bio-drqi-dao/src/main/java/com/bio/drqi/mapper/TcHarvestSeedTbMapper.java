package com.bio.drqi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.TcHarvestSeedTb;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
* @author zoujun
* @description 针对表【tc_harvest_seed_tb】的数据库操作Mapper
* @createDate 2026-06-17 14:19:03
* @Entity com.bio.drqi.domain.TcHarvestSeedTb
*/
public interface TcHarvestSeedTbMapper extends BaseMapper<TcHarvestSeedTb> {

    int insertBatch(@Param("tcHarvestSeedTbCollection") Collection<TcHarvestSeedTb> tcHarvestSeedTbCollection);

    List<TcHarvestSeedTb> selectSelective(TcHarvestSeedTb tcHarvestSeedTb);



}




