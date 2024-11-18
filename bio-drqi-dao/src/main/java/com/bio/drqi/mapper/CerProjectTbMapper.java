package com.bio.drqi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerProjectTb;

public interface CerProjectTbMapper extends BaseMapper<CerProjectTb> {

    List<CerProjectTb> selectBySelective(CerProjectTb cerProjectTb);

    List<CerProjectTb> selectListPage(CerProjectTb cerProjectTb);

    CerProjectTb selectOneByProjectCode(@Param("projectCode") String projectCode);

    CerProjectTb selectOneByTaskNum(@Param("taskNum") String taskNum);

    int deleteByTaskNum(@Param("taskNum") String taskNum);

    List<CerProjectTb> selectAllOrderById();

    List<CerProjectTb> selectAll();
    List<CerProjectTb> selectAllByOwnerUserIdIsNotNull();

    Integer selectAllCount();

}