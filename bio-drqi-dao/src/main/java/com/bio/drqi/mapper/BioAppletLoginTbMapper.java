package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BioAppletLoginTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bio_applet_login_tb(小程序登录表)】的数据库操作Mapper
* @createDate 2024-11-19 17:10:56
* @Entity com.bio.drqi.domain.BioAppletLoginTb
*/
public interface BioAppletLoginTbMapper extends BaseMapper<BioAppletLoginTb> {

    BioAppletLoginTb selectOneByTelephone(@Param("telephone") String telephone);

    BioAppletLoginTb selectOneByAppIdAndOpenId(@Param("appId") String appId, @Param("openId") String openId);

}




