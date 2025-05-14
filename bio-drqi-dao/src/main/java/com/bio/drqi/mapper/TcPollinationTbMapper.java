package com.bio.drqi.mapper;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.TcPollinationTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【tc_pollination_tb】的数据库操作Mapper
* @createDate 2025-05-14 09:13:07
* @Entity com.bio.drqi.domain.TcPollinationTb
*/
public interface TcPollinationTbMapper extends BaseMapper<TcPollinationTb> {

    List<TcPollinationTb> selectAllByPollinationApplyNum(@Param("pollinationApplyNum") String pollinationApplyNum);

    List<TcPollinationTb> selectSelective(TcPollinationTb tcPollinationTb);

    int insertBatch(@Param("tcPollinationTbCollection") Collection<TcPollinationTb> tcPollinationTbCollection);

}




