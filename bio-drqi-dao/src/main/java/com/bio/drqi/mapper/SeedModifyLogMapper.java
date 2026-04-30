package com.bio.drqi.mapper;

import com.bio.drqi.domain.SeedModifyLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
* @author zoujun
* @description 针对表【seed_modify_log】的数据库操作Mapper
* @createDate 2026-04-30 11:18:06
* @Entity com.bio.drqi.domain.SeedModifyLog
*/
public interface SeedModifyLogMapper extends BaseMapper<SeedModifyLog> {

    int insertBatch(@Param("seedModifyLogCollection") Collection<SeedModifyLog> seedModifyLogCollection);
}




