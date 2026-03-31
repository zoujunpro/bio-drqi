package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.TcPollinationSingleNumTb;
import com.bio.drqi.mapper.TcPollinationSingleNumTbMapper;
import com.bio.drqi.tc.req.TcPollinationSingleNumListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationSingleNumListPageRspDTO;
import com.bio.drqi.tc.service.TcPollinationSingleNumService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 授粉单株编号管理服务实现
 */
@Service
public class TcPollinationSingleNumServiceImpl implements TcPollinationSingleNumService {

    @Resource
    private TcPollinationSingleNumTbMapper tcPollinationSingleNumTbMapper;

    @Override
    public PageInfo<TcPollinationSingleNumListPageRspDTO> listPage(TcPollinationSingleNumListPageReqDTO tcPollinationSingleNumListPageReqDTO) {
        PageHelper.startPage(tcPollinationSingleNumListPageReqDTO.getPageNum(), tcPollinationSingleNumListPageReqDTO.getPageSize());
        TcPollinationSingleNumTb queryCondition = new TcPollinationSingleNumTb();
        queryCondition.setExperimentNum(tcPollinationSingleNumListPageReqDTO.getExperimentNum());
        queryCondition.setPollinationApplyNum(tcPollinationSingleNumListPageReqDTO.getPollinationApplyNum());
        queryCondition.setSeedNum(tcPollinationSingleNumListPageReqDTO.getSeedNum());
        queryCondition.setRegionNum(tcPollinationSingleNumListPageReqDTO.getRegionNum());
        queryCondition.setTcSingleNumber(tcPollinationSingleNumListPageReqDTO.getTcSingleNumber());

        List<TcPollinationSingleNumTb> tbList = tcPollinationSingleNumTbMapper.selectSelective(queryCondition);
        PageInfo<TcPollinationSingleNumTb> srcPageInfo = new PageInfo<>(tbList);

        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcPollinationSingleNumListPageRspDTO.class);
    }

    @Override
    public TcPollinationSingleNumListPageRspDTO detail(Integer id) {
        TcPollinationSingleNumTb tb = tcPollinationSingleNumTbMapper.selectById(id);
        if (tb == null) {
            return null;
        }
        return BeanUtils.copyProperties(tb, TcPollinationSingleNumListPageRspDTO.class);
    }

}
