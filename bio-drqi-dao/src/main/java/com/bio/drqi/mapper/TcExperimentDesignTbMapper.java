package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.TcExperimentDesignTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【tc_experiment_design_tb(田间试验设计表)】的数据库操作Mapper
* @createDate 2025-05-06 14:01:49
* @Entity com.bio.drqi.domain.TcExperimentDesignTb
*/
public interface TcExperimentDesignTbMapper extends BaseMapper<TcExperimentDesignTb> {
    int insertBatch(@Param("tcExperimentDesignTbCollection") Collection<TcExperimentDesignTb> tcExperimentDesignTbCollection);
}




