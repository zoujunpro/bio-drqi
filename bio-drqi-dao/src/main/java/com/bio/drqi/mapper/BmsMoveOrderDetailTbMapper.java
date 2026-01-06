package com.bio.drqi.mapper;
import java.util.List;

import com.bio.drqi.domain.BmsReturnOrderDetailTb;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsMoveOrderDetailTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bms_move_order_detail_tb】的数据库操作Mapper
* @createDate 2025-07-11 14:27:25
* @Entity com.bio.drqi.domain.BmsMoveOrderDetailTb
*/
public interface BmsMoveOrderDetailTbMapper extends BaseMapper<BmsMoveOrderDetailTb> {


    List<BmsMoveOrderDetailTb> selectSelective(BmsMoveOrderDetailTb bmsMoveOrderDetailTb);

    List<BmsMoveOrderDetailTb> selectForCountStockDetailList(BmsMoveOrderDetailTb bmsMoveOrderDetailTb);

}




