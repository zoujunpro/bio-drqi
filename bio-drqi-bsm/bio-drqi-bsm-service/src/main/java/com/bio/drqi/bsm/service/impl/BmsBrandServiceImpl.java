package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.lang.UUID;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.req.BmsBrandAddReqDTO;
import com.bio.drqi.bsm.req.BmsBrandEditReqDTO;
import com.bio.drqi.bsm.req.BmsBrandListPageReqDTO;
import com.bio.drqi.bsm.req.BmsBrandQueryListReqDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsBrandQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsBrandService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.BmsBrandTb;
import com.bio.drqi.domain.BmsSupplierTb;
import com.bio.drqi.mapper.BmsBrandTbMapper;
import com.bio.drqi.mapper.BmsProductTbMapper;
import com.bio.drqi.mapper.BmsSupplierTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class BmsBrandServiceImpl implements BmsBrandService {

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Override
    public PageInfo<BmsBrandListPageRspDTO> listPage(BmsBrandListPageReqDTO bmsBrandListPageReqDTO) {
        PageHelper.startPage(bmsBrandListPageReqDTO.getPageNum(), bmsBrandListPageReqDTO.getPageSize());
        List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(BmsBrandTb.builder().brandName(bmsBrandListPageReqDTO.getBrandName()).deleteFlag(BioDrQiContents.N).build());
        PageInfo<BmsBrandTb> srcPageInfo = new PageInfo<>(bmsBrandTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsBrandListPageRspDTO.class);
    }

    @Override
    public List<BmsBrandQueryListRspDTO> queryList(BmsBrandQueryListReqDTO bmsBrandQueryListReqDTO) {
        List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(BmsBrandTb.builder().supplierCode(bmsBrandQueryListReqDTO.getSupplierCode()).deleteFlag(BioDrQiContents.N).build());
        return BeanUtils.copyListProperties(bmsBrandTbList, BmsBrandQueryListRspDTO.class);
    }

    @Override
    public List<BmsBrandListAllRspDTO> listAll() {
        List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(BmsBrandTb.builder().deleteFlag(BioDrQiContents.N).build());
        return BeanUtils.copyListProperties(bmsBrandTbList, BmsBrandListAllRspDTO.class);
    }

    @Override
    public void add(BmsBrandAddReqDTO bmsBrandAddReqDTO) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneBySupplierCodeAndBrandName(bmsBrandAddReqDTO.getSupplierCode(), bmsBrandAddReqDTO.getBrandName());
        if (Objects.nonNull(bmsBrandTb)&&BioDrQiContents.N.equals(bmsBrandTb.getDeleteFlag())) {
            throw new BusinessException("该品牌已经存在");
        }
        if(bmsBrandTb==null){
            bmsBrandTb.setSupplierCode(bmsBrandAddReqDTO.getSupplierCode());
            bmsBrandTb.setBrandCode(IdUtils.simpleUUID());
            bmsBrandTb.setBrandName(bmsBrandAddReqDTO.getBrandName());
            bmsBrandTb.setCreateTime(new Date());
            bmsBrandTb.setCreateUserId(SecurityContextHolder.getUserId());
            bmsBrandTb.setCreateUserName(SecurityContextHolder.getNickName());
            bmsBrandTb.setDeleteFlag(BioDrQiContents.N);
        }else {
            if(!bmsBrandTb.getSupplierCode().equals(bmsBrandAddReqDTO.getSupplierCode())){
                BmsSupplierTb bmsSupplierTb=bmsSupplierTbMapper.selectOneBySupplierCode(bmsBrandTb.getSupplierCode());
                throw new BusinessException("此品牌曾经使用过，且隶属于"+bmsSupplierTb.getSupplierName());
            }
            bmsBrandTb.setCreateTime(new Date());
            bmsBrandTb.setCreateUserId(SecurityContextHolder.getUserId());
            bmsBrandTb.setCreateUserName(SecurityContextHolder.getNickName());
            bmsBrandTb.setDeleteFlag(BioDrQiContents.Y);
        }

        bmsBrandTbMapper.insert(bmsBrandTb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectById(id);
        if (Objects.isNull(bmsBrandTb)) {
            throw new BusinessException("品牌不存在");
        }
        bmsBrandTb.setDeleteFlag(BioDrQiContents.Y);
        bmsBrandTbMapper.updateById(bmsBrandTb);

        //更新该品牌下商品信息为禁用
        bmsProductTbMapper.updateDeleteFlagBySupplierCode(BioDrQiContents.Y, bmsBrandTb.getBrandCode());


    }

    @Override
    public void edit(BmsBrandEditReqDTO bmsBrandEditReqDTO) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectById(bmsBrandEditReqDTO.getId());
        if (Objects.isNull(bmsBrandTb)) {
            throw new BusinessException("品牌不存在");
        }
        bmsBrandTb.setBrandName(bmsBrandEditReqDTO.getBrandName());
        bmsBrandTbMapper.updateById(bmsBrandTb);

    }

}
