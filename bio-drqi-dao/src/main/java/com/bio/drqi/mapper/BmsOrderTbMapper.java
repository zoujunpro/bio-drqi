package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsOrderTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bms_order_tb(订单信息表)】的数据库操作Mapper
* @createDate 2025-03-12 11:10:25
* @Entity com.bio.drqi.domain.BmsOrderTb
*/
public interface BmsOrderTbMapper extends BaseMapper<BmsOrderTb> {

    BmsOrderTb selectOneByTaskNum(@Param("taskNum") String taskNum);

}




