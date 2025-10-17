package com.bio.drqi.mapper;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.TcPollinationSingleNumTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【tc_pollination_single_num_tb(授粉无取样编号的单珠编号区间)】的数据库操作Mapper
* @createDate 2025-05-29 09:40:15
* @Entity com.bio.drqi.domain.TcPollinationSingleNumTb
*/
public interface TcPollinationSingleNumTbMapper extends BaseMapper<TcPollinationSingleNumTb> {
    List<TcPollinationSingleNumTb> selectAllByExperimentNumAndPollinationApplyNumIsNull(@Param("experimentNum") String experimentNum);

    int deleteByExperimentNumAndPollinationApplyNumIsNull(@Param("experimentNum") String experimentNum);

    int insertBatch(@Param("tcPollinationSingleNumTbCollection") Collection<TcPollinationSingleNumTb> tcPollinationSingleNumTbCollection);

    TcPollinationSingleNumTb selectOneBySingleNumber(@Param("singleNumber") String singleNumber);

    int updatePollinationApplyNumIsNullByPollinationApplyNum(@Param("pollinationApplyNum") String pollinationApplyNum);

    List<TcPollinationSingleNumTb> selectAllByExperimentNumAndRegionNumOrderByIdDesc(@Param("experimentNum") String experimentNum, @Param("regionNum") String regionNum);

    int updatePollinationApplyNumByExperimentNumAndPollinationApplyNumIsNull(@Param("pollinationApplyNum") String pollinationApplyNum, @Param("experimentNum") String experimentNum);

}




