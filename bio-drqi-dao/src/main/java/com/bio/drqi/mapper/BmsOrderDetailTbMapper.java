package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsOrderDetailTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bms_order_detail_tb(订单明细表)】的数据库操作Mapper
* @createDate 2025-03-14 10:01:54
* @Entity com.bio.drqi.domain.BmsOrderDetailTb
*/
public interface BmsOrderDetailTbMapper extends BaseMapper<BmsOrderDetailTb> {

    List<BmsOrderDetailTb> selectAllByOrderNum(@Param("orderNum") String orderNum);

    List<BmsOrderDetailTb> selectSelective(BmsOrderDetailTb bmsOrderDetailTb);

}




