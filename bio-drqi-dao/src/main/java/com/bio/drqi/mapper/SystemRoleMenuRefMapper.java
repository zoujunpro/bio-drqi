package com.bio.drqi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SystemRoleMenuRef;

import java.util.List;

public interface SystemRoleMenuRefMapper extends BaseMapper<SystemRoleMenuRef> {
    int deleteByMenuId(Integer menuId);

    int deleteByRoleId(Integer roleId);

    int batchInsert(List<SystemRoleMenuRef> list);
}