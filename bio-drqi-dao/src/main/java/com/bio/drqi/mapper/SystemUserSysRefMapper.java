package com.bio.drqi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SystemUserSysRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemUserSysRefMapper extends BaseMapper<SystemUserSysRef> {
    int deleteByUserId(@Param("userId") Integer userId);
    int deleteBySystemId(@Param("systemId") Integer systemId);
    int batchInsert(@Param("list") List<SystemUserSysRef> list);
}