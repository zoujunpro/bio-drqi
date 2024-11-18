package com.bio.drqi.mapper;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.SeedStockOutLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【seed_out_store_log】的数据库操作Mapper
* @createDate 2023-12-20 14:08:36
* @Entity com.bio.cer.domain.SeedOutStoreLog
*/
public interface SeedStockOutLogMapper extends BaseMapper<SeedStockOutLog> {


    List<SeedStockOutLog> selectSelective(SeedStockOutLog seedStockOutLog);

    List<SeedStockOutLog> selectAllByIdIn(@Param("idList") Collection<Integer> idList);

    List<SeedStockOutLog> selectAllByTaskNum(@Param("taskNum") String taskNum);

}




