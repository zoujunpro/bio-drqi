package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SystemSysTb;

import java.util.List;

public interface SystemSysTbMapper extends BaseMapper<SystemSysTb> {

    List<SystemSysTb> querySystemSysTbListByUserId(Integer userId);

    List<SystemSysTb> selectAllByStatus(@Param("status") String status);
}