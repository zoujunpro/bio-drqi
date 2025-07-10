package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.bio.drqi.domain.BmsProjectDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bms_project_dict(耗材管理项目表)】的数据库操作Mapper
* @createDate 2025-04-10 15:17:59
* @Entity com.bio.drqi.domain.BmsProjectDict
*/
public interface BmsProjectDictMapper extends BaseMapper<BmsProjectDict> {

    List<BmsProjectDict> selectAllOrderByIdDesc();

    BmsProjectDict selectOneByProjectCode(@Param("projectCode") String projectCode);


    List<BmsProjectDict> selectSelective(BmsProjectDict bmsProjectDict);

    int updateKdNumberByKdProjectCode(@Param("kdNumber") String kdNumber, @Param("kdProjectCode") String kdProjectCode);
}




