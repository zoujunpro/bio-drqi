package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.SystemDeptTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【system_dept_tb】的数据库操作Mapper
* @createDate 2024-02-22 17:22:46
* @Entity com.bio.cer.domain.SystemDeptTb
*/
public interface SystemDeptTbMapper extends BaseMapper<SystemDeptTb> {

    List<SystemDeptTb> selectAllByParentId(@Param("parentId") Integer parentId);

}




