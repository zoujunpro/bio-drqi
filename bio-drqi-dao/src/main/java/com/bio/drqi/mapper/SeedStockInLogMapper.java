package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.SeedStockInLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【seed_in_store_log】的数据库操作Mapper
* @createDate 2023-12-20 13:33:35
* @Entity com.bio.cer.domain.SeedInStoreLog
*/
public interface SeedStockInLogMapper extends BaseMapper<SeedStockInLog> {

    List<SeedStockInLog> selectSelective(SeedStockInLog seedStockInLog);

    List<SeedStockInLog> selectAllByTaskNum(@Param("taskNum") String taskNum);


    SeedStockInLog selectOneByUniqueCode(@Param("uniqueCode") String uniqueCode);

    SeedStockInLog selectOneBySeedNum(@Param("seedNum") String seedNum);

    int deleteByTaskNum(@Param("taskNum") String taskNum);
}




