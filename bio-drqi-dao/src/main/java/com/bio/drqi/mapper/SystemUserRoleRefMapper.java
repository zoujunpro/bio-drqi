package com.bio.drqi.mapper;
import com.bio.drqi.domain.SystemUserRoleRef;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SystemUserRoleRefMapper extends BaseMapper<SystemUserRoleRef> {

    int deleteByRoleId(Integer roleId);

    int deleteByUserId(Integer userId);

    int batchInsert(List<SystemUserRoleRef> list);

    List<SystemUserRoleRef> selectAllByRoleIdIn(@Param("roleIdList") Collection<Integer> roleIdList);

}