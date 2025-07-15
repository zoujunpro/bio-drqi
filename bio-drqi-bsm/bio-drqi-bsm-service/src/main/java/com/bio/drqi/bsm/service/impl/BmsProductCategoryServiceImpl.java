package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.kd.enums.KdFCategoryIDEnum;
import com.bio.drqi.bsm.kd.enums.KdParentGroupEnum;
import com.bio.drqi.bsm.req.BmsProductCategoryAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductCategoryEditReqDTO;
import com.bio.drqi.bsm.req.BmsProductCategoryListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductCategoryListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductCategoryListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductCategoryService;
import com.bio.drqi.domain.BmsProductCategoryTb;
import com.bio.drqi.mapper.BmsProductCategoryTbMapper;
import com.bio.drqi.mapper.BmsProductTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Service
public class BmsProductCategoryServiceImpl implements BmsProductCategoryService {

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Value("${spring.profiles.active}")
    private String active;

    @Override
    public PageInfo<BmsProductCategoryListPageRspDTO> listPage(BmsProductCategoryListPageReqDTO bmsProductCategoryListPageReqDTO) {
        PageHelper.startPage(bmsProductCategoryListPageReqDTO.getPageNum(), bmsProductCategoryListPageReqDTO.getPageSize());
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(BmsProductCategoryTb.builder().productCategoryName(bmsProductCategoryListPageReqDTO.getProductCategoryName()).build());
        PageInfo<BmsProductCategoryTb> srcPageInfo = new PageInfo<>(bmsProductCategoryTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductCategoryListPageRspDTO.class);
    }

    @Override
    public List<BmsProductCategoryListAllRspDTO> listAll() {
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
        return BeanUtils.copyListProperties(bmsProductCategoryTbList, BmsProductCategoryListAllRspDTO.class);
    }

    @Override
    public void add(BmsProductCategoryAddReqDTO bmsProductCategoryAddReqDTO) {
        KdFCategoryIDEnum kdFCategoryIDEnum = KdFCategoryIDEnum.valueOfName(bmsProductCategoryAddReqDTO.getKdCategoryCode());
        KdParentGroupEnum kdParentGroupEnum = KdParentGroupEnum.ofCode(bmsProductCategoryAddReqDTO.getKdParentId(), active);
        BmsProductCategoryTb bmsProductCategoryTb = new BmsProductCategoryTb();
        bmsProductCategoryTb.setProductCategoryName(bmsProductCategoryAddReqDTO.getProductCategoryName());
        bmsProductCategoryTb.setProductCategoryCode(IdUtils.simpleUUID());
        bmsProductCategoryTb.setKdCategoryCode(kdFCategoryIDEnum.name());
        bmsProductCategoryTb.setKdCategoryName(kdFCategoryIDEnum.desc);
        bmsProductCategoryTb.setKdParentId(kdParentGroupEnum.code);
        bmsProductCategoryTb.setKdParentName(kdParentGroupEnum.name);
        bmsProductCategoryTb.setCreateTime(new Date());
        bmsProductCategoryTb.setCreateUserId(SecurityContextHolder.getUserId());
        bmsProductCategoryTb.setCreateUserName(SecurityContextHolder.getNickName());
        try {
            bmsProductCategoryTbMapper.insert(bmsProductCategoryTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("重复添加");
        }
    }

    @Override
    public void delete(Integer id) {
        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectById(id);
        if (bmsProductCategoryTb == null) {
            throw new BusinessException("找不到数据");
        }
        if (CollectionUtil.isNotEmpty(bmsProductTbMapper.selectAllByProductCategoryCode(bmsProductCategoryTb.getProductCategoryCode()))) {
            throw new BusinessException("该类别已经使用，不能删除");
        }
        bmsProductCategoryTbMapper.deleteById(id);

    }

    @Override
    public void edit(BmsProductCategoryEditReqDTO bmsProductCategoryEditReqDTO) {
        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectById(bmsProductCategoryEditReqDTO.getId());
        if (bmsProductCategoryTb == null) {
            throw new BusinessException("找不到数据");
        }
        KdFCategoryIDEnum kdFCategoryIDEnum = KdFCategoryIDEnum.valueOfName(bmsProductCategoryEditReqDTO.getKdCategoryCode());
        KdParentGroupEnum kdParentGroupEnum = KdParentGroupEnum.ofCode(bmsProductCategoryEditReqDTO.getKdParentId(), active);
        bmsProductCategoryTb.setProductCategoryName(bmsProductCategoryEditReqDTO.getProductCategoryName());
        bmsProductCategoryTb.setKdCategoryCode(kdFCategoryIDEnum.name());
        bmsProductCategoryTb.setKdCategoryName(kdFCategoryIDEnum.desc);
        bmsProductCategoryTb.setKdParentId(kdParentGroupEnum.code);
        bmsProductCategoryTb.setKdParentName(kdParentGroupEnum.name);
        try {
            bmsProductCategoryTbMapper.updateById(bmsProductCategoryTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("已经存在此名称");
        }
    }
}
