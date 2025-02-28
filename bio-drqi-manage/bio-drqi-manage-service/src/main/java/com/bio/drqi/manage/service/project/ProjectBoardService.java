package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.board.*;

import java.util.List;

public interface ProjectBoardService {
    ProjectTaskCountRspDTO taskCount();


    List<CountTransByMonthRspDTO> countTransByMonth(String year);
    List<CountSampleByMonthRspDTO> countSampleByMonth(String year);

    List<VectorTaskListBoardRspDTO> vectorTaskListBoard(VectorTaskListBoardReqDTO vectorTaskListBoardReqDTO);


}

