package com.bio.drqi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.TcPollinationApplyTb;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【tc_pollination_apply_tb(田测授粉申请表)】的数据库操作Mapper
* @createDate 2025-05-14 09:13:07
* @Entity com.bio.drqi.domain.TcPollinationApplyTb
*/
public interface TcPollinationApplyTbMapper extends BaseMapper<TcPollinationApplyTb> {
    List<TcPollinationApplyTb>  selectSelective(TcPollinationApplyTb tcPollinationApplyTb);

    TcPollinationApplyTb selectOneByPollinationApplyNum(@Param("pollinationApplyNum") String pollinationApplyNum);


    TcPollinationApplyTb selectOneByExperimentNum(@Param("experimentNum") String experimentNum);


    List<TcPollinationApplyTb> selectAllByHarvestApplyNumIsNullOrderByIdDesc();
}




