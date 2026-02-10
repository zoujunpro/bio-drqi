package com.bio.drqi.mapper;

import com.bio.drqi.domain.SystemUserTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author zou'jun
 * @description 针对表【system_user_tb(用户表)】的数据库操作Mapper
 * @createDate 2024-02-22 17:04:20
 * @Entity com.bio.cer.domain.SystemUserTb
 */
public interface SystemUserTbMapper extends BaseMapper<SystemUserTb> {


    List<SystemUserTb> selectAllByDeptIdIn(@Param("deptIdList") Collection<Integer> deptIdList);

    SystemUserTb  selectDeptLeaderByDeptName(@Param("deptName") String deptName);

    SystemUserTb selectOneByEmail(@Param("email") String email);



}




