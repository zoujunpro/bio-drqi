package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SystemMenuTb;

import java.util.List;

public interface SystemMenuTbMapper extends BaseMapper<SystemMenuTb> {

    List<SystemMenuTb> queryMenuByUserId(Integer userId);

    List<SystemMenuTb> selectAllBySystemId(@Param("systemId") Integer systemId);

    List<SystemMenuTb> selectAllByParentId(@Param("parentId") Integer parentId);

}