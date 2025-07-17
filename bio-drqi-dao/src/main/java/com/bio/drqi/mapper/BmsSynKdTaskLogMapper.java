package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsSynKdTaskLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bms_syn_kd_task_log】的数据库操作Mapper
* @createDate 2025-07-17 09:06:18
* @Entity com.bio.drqi.domain.BmsSynKdTaskLog
*/
public interface BmsSynKdTaskLogMapper extends BaseMapper<BmsSynKdTaskLog> {

    List<BmsSynKdTaskLog> selectAllBySynStatus(@Param("synStatus") String synStatus);

    List<BmsSynKdTaskLog> selectSelective(BmsSynKdTaskLog bmsSynKdTaskLog);

}




