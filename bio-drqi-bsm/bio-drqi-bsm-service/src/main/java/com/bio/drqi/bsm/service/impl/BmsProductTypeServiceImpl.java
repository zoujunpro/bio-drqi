package com.bio.drqi.bsm.service.impl;

import com.bio.drqi.bsm.req.BmsProductTyAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductTyEditReqDTO;
import com.bio.drqi.bsm.req.BmsProductTyListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductTypeService;
import com.bio.drqi.domain.BmsProductTypeTb;
import com.bio.drqi.mapper.BmsProductTypeTbMapper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class BmsProductTypeServiceImpl implements BmsProductTypeService {

    @Resource
    private BmsProductTypeTbMapper bmsProductTypeTbMapper;

    @Override
    public PageInfo<BmsProductTyListPageRspDTO> listPage(BmsProductTyListPageReqDTO bmsProductTyListPageReqDTO) {
        bmsProductTypeTbMapper.selectSelective(BmsProductTypeTb.builder().productTypeName(bmsProductTyListPageReqDTO.getProductTypeName()).build());
        return null;
    }

    @Override
    public List<BmsProductTyListAllRspDTO> listAll() {
        return null;
    }

    @Override
    public void add(BmsProductTyAddReqDTO bmsProductTyAddReqDTO) {

    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void edit(BmsProductTyEditReqDTO bmsProductTyEditReqDTO) {

    }
}
