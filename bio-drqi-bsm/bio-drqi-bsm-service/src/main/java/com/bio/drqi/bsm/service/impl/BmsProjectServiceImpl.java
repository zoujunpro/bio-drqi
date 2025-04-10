package com.bio.drqi.bsm.service.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProjectQueryAllReqDTO;
import com.bio.drqi.bsm.service.BmsProjectService;
import com.bio.drqi.domain.BmsProjectDict;
import com.bio.drqi.mapper.BmsProjectDictMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BmsProjectServiceImpl implements BmsProjectService {


    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Override
    public List<BmsProjectQueryAllReqDTO> queryAll() {
        List<BmsProjectDict> bmsProjectDictList = bmsProjectDictMapper.selectAllOrderByIdDesc();
        return BeanUtils.copyListProperties(bmsProjectDictList,BmsProjectQueryAllReqDTO.class);
    }
}
