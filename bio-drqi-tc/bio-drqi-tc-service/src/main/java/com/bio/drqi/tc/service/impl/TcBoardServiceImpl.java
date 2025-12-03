package com.bio.drqi.tc.service.impl;

import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.tc.rsp.TcBoardChartOneRspDTO;
import com.bio.drqi.tc.service.TcBoardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TcBoardServiceImpl implements TcBoardService {

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Override
    public TcBoardChartOneRspDTO chartOne() {
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllForBroadOne();

        return null;
    }
}
