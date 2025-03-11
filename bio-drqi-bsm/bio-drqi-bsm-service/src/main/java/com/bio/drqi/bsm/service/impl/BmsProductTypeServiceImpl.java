package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.req.BmsProductTypeAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductTypeEditReqDTO;
import com.bio.drqi.bsm.req.BmsProductTypeListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductTypeService;
import com.bio.drqi.domain.BmsProductTb;
import com.bio.drqi.domain.BmsProductTypeTb;
import com.bio.drqi.mapper.BmsProductTbMapper;
import com.bio.drqi.mapper.BmsProductTypeTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BmsProductTypeServiceImpl implements BmsProductTypeService {

    @Resource
    private BmsProductTypeTbMapper bmsProductTypeTbMapper;

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Override
    public PageInfo<BmsProductTyListPageRspDTO> listPage(BmsProductTypeListPageReqDTO bmsProductTypeListPageReqDTO) {
        PageHelper.startPage(bmsProductTypeListPageReqDTO.getPageNum(), bmsProductTypeListPageReqDTO.getPageSize());
        List<BmsProductTypeTb> bmsProductTypeTbList = bmsProductTypeTbMapper.selectSelective(BmsProductTypeTb.builder().productTypeName(bmsProductTypeListPageReqDTO.getProductTypeName()).build());
        PageInfo<BmsProductTypeTb> srcPageInfo = new PageInfo<>(bmsProductTypeTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductTyListPageRspDTO.class);
    }

    @Override
    public List<BmsProductTyListAllRspDTO> listAll() {
        List<BmsProductTypeTb> bmsProductTypeTbList = bmsProductTypeTbMapper.selectSelective(null);
        return BeanUtils.copyListProperties(bmsProductTypeTbList, BmsProductTyListAllRspDTO.class);
    }

    @Override
    public void add(BmsProductTypeAddReqDTO bmsProductTypeAddReqDTO) {
        BmsProductTypeTb bmsProductTypeTb = new BmsProductTypeTb();
        bmsProductTypeTb.setProductTypeCode(IdUtils.simpleUUID());
        bmsProductTypeTb.setProductTypeName(bmsProductTypeAddReqDTO.getProductTypeName());
        bmsProductTypeTb.setCreateTime(new Date());
        bmsProductTypeTb.setCreateUserId(SecurityContextHolder.getUserId());
        bmsProductTypeTb.setCreateUserName(SecurityContextHolder.getNickName());
        try {
            bmsProductTypeTbMapper.insert(bmsProductTypeTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("不能重复添加");
        }
    }

    @Override
    public void delete(Integer id) {
        BmsProductTypeTb bmsProductTypeTb = bmsProductTypeTbMapper.selectById(id);
        if(bmsProductTypeTb==null){
            throw new BusinessException("材料类型不存在");
        }
       List<BmsProductTb> bmsProductTbList= bmsProductTbMapper.selectAllByProductTypeCode(bmsProductTypeTb.getProductTypeCode());
        if(CollectionUtil.isNotEmpty(bmsProductTbList)){
            throw new BusinessException("此材料类型已经使用，无法删除");
        }
        bmsProductTypeTbMapper.deleteById(id);
    }

    @Override
    public void edit(BmsProductTypeEditReqDTO bmsProductTypeEditReqDTO) {
        BmsProductTypeTb bmsProductTypeTb = bmsProductTypeTbMapper.selectById(bmsProductTypeEditReqDTO.getId());
        if(bmsProductTypeTb==null){
            throw new BusinessException("材料类型不存在");
        }
        bmsProductTypeTb.setProductTypeName(bmsProductTypeEditReqDTO.getProductTypeName());
        try {
            bmsProductTypeTbMapper.updateById(bmsProductTypeTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("名称已经存在");
        }
    }
}
