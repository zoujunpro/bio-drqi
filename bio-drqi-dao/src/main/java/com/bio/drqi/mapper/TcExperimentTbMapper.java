package com.bio.drqi.mapper;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.TcExperimentTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * @author zou'jun
 * @description 针对表【tc_experiment_tb(田测实验表)】的数据库操作Mapper
 * @createDate 2025-05-06 14:01:49
 * @Entity com.bio.drqi.domain.TcExperimentTb
 */
public interface TcExperimentTbMapper extends BaseMapper<TcExperimentTb> {

    List<TcExperimentTb> selectAllByExperimentNumInAndExperimentTypeLike(@Param("experimentNumList") Collection<String> experimentNumList, @Param("experimentType") String experimentType);

    List<TcExperimentTb> selectSelective(TcExperimentTb tcExperimentTb);

    List<TcExperimentTb> selectAllByPollinationNumIsNullOrderByIdDesc();



    TcExperimentTb selectOneByExperimentNum(@Param("experimentNum") String experimentNum);

    List<TcExperimentTb> selectAllByExperimentStatusOrderByIdDesc(@Param("experimentStatus") String experimentStatus);

    List<TcExperimentTb> selectAllByExperimentStatusAndHarvestApplyNumIsNullOrderByIdDesc(@Param("experimentStatus") String experimentStatus);

    String selectMaxSampleCodePerfix();

}



