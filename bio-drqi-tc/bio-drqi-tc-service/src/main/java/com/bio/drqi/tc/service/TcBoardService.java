package com.bio.drqi.tc.service;

import com.bio.drqi.tc.rsp.TcBoardChartOneRspDTO;
import com.bio.drqi.tc.rsp.TcBoardCountRspDTO;

import java.util.List;

public interface TcBoardService {

    List<TcBoardChartOneRspDTO> chartOne();

    TcBoardCountRspDTO count();
}
