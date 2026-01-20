package com.bio.drqi.bsm.service.impl;

import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.req.BmsBrandAddReqDTO;
import com.bio.drqi.bsm.req.BmsBrandEditReqDTO;
import com.bio.drqi.bsm.req.BmsBrandListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListPageRspDTO;
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

    @Override
    public PageInfo<BmsBrandListPageRspDTO> listPage(BmsBrandListPageReqDTO bmsBrandListPageReqDTO) {
        PageHelper.startPage(bmsBrandListPageReqDTO.getPageNum(), bmsBrandListPageReqDTO.getPageSize());
        List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(BeanUtils.copyProperties(bmsBrandListPageReqDTO, BmsBrandTb.class));
        PageInfo<BmsBrandTb> srcPageInfo = new PageInfo<>(bmsBrandTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsBrandListPageRspDTO.class);
    }


    @Override
    public List<BmsBrandListAllRspDTO> listAll() {
        List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(BmsBrandTb.builder().brandStatus(BioDrQiContents.Y).build());
        return BeanUtils.copyListProperties(bmsBrandTbList, BmsBrandListAllRspDTO.class);
    }

    @Override
    public BmsBrandTb add(BmsBrandAddReqDTO bmsBrandAddReqDTO) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsBrandAddReqDTO.getBrandName());
        if (Objects.nonNull(bmsBrandTb)) {
            if (BioDrQiContents.N.equals(bmsBrandTb.getBrandStatus())) {
                throw new BusinessException("该品牌已经存在,且是禁用,无需添加，启用即可");
            }else {
                throw new BusinessException("该品牌已经存在,无需添加");
            }
        }

        bmsBrandTb = new BmsBrandTb();
        bmsBrandTb.setBrandCode(IdUtils.simpleUUID());
        bmsBrandTb.setBrandName(bmsBrandAddReqDTO.getBrandName());
        bmsBrandTb.setCreateTime(new Date());
        bmsBrandTb.setCreateUserId(SecurityContextHolder.getUserId());
        bmsBrandTb.setCreateUserName(SecurityContextHolder.getNickName());
        bmsBrandTb.setBrandStatus(BioDrQiContents.Y);
        bmsBrandTbMapper.insert(bmsBrandTb);
        return bmsBrandTb;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Integer id) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectById(id);
        if (Objects.isNull(bmsBrandTb)) {
            throw new BusinessException("品牌不存在");
        }
        bmsBrandTb.setBrandStatus(BioDrQiContents.N);
        bmsBrandTbMapper.updateById(bmsBrandTb);

    }

    @Override
    public void enable(Integer id) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectById(id);
        if (Objects.isNull(bmsBrandTb)) {
            throw new BusinessException("品牌不存在");
        }
        bmsBrandTb.setBrandStatus(BioDrQiContents.Y);
        bmsBrandTbMapper.updateById(bmsBrandTb);
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
