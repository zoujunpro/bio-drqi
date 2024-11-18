package com.bio.drqi.mapper;
import java.util.List;

import com.bio.drqi.domain.SeedStockDestructionLog;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【seed_destruction_log】的数据库操作Mapper
 * @createDate 2023-12-27 11:16:15
 * @Entity com.bio.cer.domain.SeedDestructionLog
 */
public interface SeedStockDestructionLogMapper extends BaseMapper<SeedStockDestructionLog> {


    List<SeedStockDestructionLog> selectSelective(SeedStockDestructionLog seedStockDestructionLog);

    SeedStockDestructionLog selectOneBySeedNum(@Param("seedNum") String seedNum);

    SeedStockDestructionLog selectOneBySeedNumAndApplyTaskNum(@Param("seedNum") String seedNum, @Param("applyTaskNum") String applyTaskNum);

}




