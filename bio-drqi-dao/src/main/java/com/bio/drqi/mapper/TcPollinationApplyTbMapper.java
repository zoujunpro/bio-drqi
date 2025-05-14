package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.TcPollinationApplyTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【tc_pollination_apply_tb(田测授粉申请表)】的数据库操作Mapper
* @createDate 2025-05-14 09:13:07
* @Entity com.bio.drqi.domain.TcPollinationApplyTb
*/
public interface TcPollinationApplyTbMapper extends BaseMapper<TcPollinationApplyTb> {
    List<TcPollinationApplyTb> selectAllByPollinationApplyNum(@Param("pollinationApplyNum") String pollinationApplyNum);

    List<TcPollinationApplyTb>  selectSelective(TcPollinationApplyTb tcPollinationApplyTb);
}




