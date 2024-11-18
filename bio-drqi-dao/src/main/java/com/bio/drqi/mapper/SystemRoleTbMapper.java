package com.bio.drqi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SystemRoleTb;

import java.util.List;

public interface SystemRoleTbMapper extends BaseMapper<SystemRoleTb> {
    List<SystemRoleTb> queryRoleListByUserId(Integer userId);

}