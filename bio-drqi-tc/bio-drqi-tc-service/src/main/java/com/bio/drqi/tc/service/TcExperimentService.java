package com.bio.drqi.tc.service;


import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.tc.enums.SampleTestCheckResultEnum;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentListPageRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface TcExperimentService {


    PageInfo<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO) ;
}
