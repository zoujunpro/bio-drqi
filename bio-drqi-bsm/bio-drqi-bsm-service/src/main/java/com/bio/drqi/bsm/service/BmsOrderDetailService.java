package com.bio.drqi.bsm.service;


import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;

import java.util.List;

public interface BmsOrderDetailService {


    List<BmsOrderDetailQueryByOrderNumRspDTO> queryByOrderNum(String orderNum);
}
