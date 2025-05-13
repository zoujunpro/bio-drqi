package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.TcSampleTestApplyTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author zou'jun
* @description 针对表【tc_sample_test_apply_tb】的数据库操作Mapper
* @createDate 2025-05-12 10:45:09
* @Entity com.bio.drqi.domain.TcSampleTestApplyTb
*/
public interface TcSampleTestApplyTbMapper extends BaseMapper<TcSampleTestApplyTb> {

    TcSampleTestApplyTb selectOneByTaskNum(@Param("taskNum") String taskNum);


    List<TcSampleTestApplyTb> selectSelective(TcSampleTestApplyTb tcSampleTestApplyTb);



}




