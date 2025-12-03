package com.bio.drqi.tc.service.impl;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.ExperimentTypeEnum;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.tc.rsp.TcBoardChartOneRspDTO;
import com.bio.drqi.tc.service.TcBoardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TcBoardServiceImpl implements TcBoardService {

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Override
    public List<TcBoardChartOneRspDTO> chartOne() {
        List<TcBoardChartOneRspDTO> resultList = new ArrayList<>();
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllForBroadOne();
        Map<String, List<TcExperimentDesignTb>> map = tcExperimentDesignTbList.stream().collect(Collectors.groupingBy(tcExperimentDesignTb -> tcExperimentDesignTb.getPdImplementCode() + "|" + tcExperimentDesignTb.getVectorTaskCode()));
        map.forEach((key, list) -> {
            TcBoardChartOneRspDTO tcBoardChartOneRspDTO = new TcBoardChartOneRspDTO();
            tcBoardChartOneRspDTO.setPdImplementCode(key.split("\\|")[0].replace("null",""));
            tcBoardChartOneRspDTO.setVectorTaskCode(key.split("\\|")[1].replace("null",""));
            list.forEach(tcExperimentDesignTb -> {
                TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcExperimentDesignTb.getExperimentNum());
                List<String> experimentTypeList = new ArrayList<>();
                if (StringUtils.isNotEmpty(tcExperimentTb.getExperimentType())) {
                    experimentTypeList.addAll(JSONUtil.toList(tcExperimentTb.getExperimentType(), String.class));
                }
                for (ExperimentTypeEnum experimentTypeEnum : ExperimentTypeEnum.values()) {
                    tcBoardChartOneRspDTO.buildExperimentTypeList(experimentTypeEnum.code, experimentTypeEnum.desc, experimentTypeList.contains(experimentTypeEnum.code) ? "Y" : "N");
                }
            });
            resultList.add(tcBoardChartOneRspDTO);
        });


        return resultList;
    }
}
