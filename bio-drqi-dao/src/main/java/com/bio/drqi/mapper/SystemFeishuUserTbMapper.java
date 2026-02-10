package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.SystemFeishuUserTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【system_feishu_user_tb(飞书用户同步表)】的数据库操作Mapper
* @createDate 2026-02-10 15:27:34
* @Entity com.bio.drqi.domain.SystemFeishuUserTb
*/
public interface SystemFeishuUserTbMapper extends BaseMapper<SystemFeishuUserTb> {

    SystemFeishuUserTb selectOneByLocalUserId(@Param("localUserId") Integer localUserId);
}




