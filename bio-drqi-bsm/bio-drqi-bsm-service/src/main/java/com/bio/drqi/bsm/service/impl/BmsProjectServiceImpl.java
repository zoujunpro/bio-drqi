package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProjectAddReqDTO;
import com.bio.drqi.bsm.req.BmsProjectEditReqDTO;
import com.bio.drqi.bsm.req.BmsProjectListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProjectQueryAllReqDTO;
import com.bio.drqi.bsm.rsp.BmsProjectListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProjectService;
import com.bio.drqi.domain.BmsOrderDetailTb;
import com.bio.drqi.domain.BmsProjectDict;
import com.bio.drqi.mapper.BmsOrderDetailTbMapper;
import com.bio.drqi.mapper.BmsProjectDictMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class BmsProjectServiceImpl implements BmsProjectService {


    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Override
    public PageInfo<BmsProjectListPageRspDTO> listPage(BmsProjectListPageReqDTO bmsProjectListPageReqDTO) {
        PageHelper.startPage(bmsProjectListPageReqDTO.getPageNum(), bmsProjectListPageReqDTO.getPageSize());
        List<BmsProjectDict> bmsProjectDictList = bmsProjectDictMapper.selectSelective(BeanUtils.copyProperties(bmsProjectListPageReqDTO, BmsProjectDict.class));
        PageInfo<BmsProjectDict> srcPageInfo = new PageInfo<>(bmsProjectDictList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProjectListPageRspDTO.class);
    }

    @Override
    public List<BmsProjectQueryAllReqDTO> queryAll() {
        List<BmsProjectDict> bmsProjectDictList = bmsProjectDictMapper.selectAllOrderByIdDesc();
        return BeanUtils.copyListProperties(bmsProjectDictList, BmsProjectQueryAllReqDTO.class);
    }

    @Override
    public void add(BmsProjectAddReqDTO bmsProjectAddReqDTO) {
        BmsProjectDict bmsProjectDict = new BmsProjectDict();
        bmsProjectDict.setProjectCode(bmsProjectAddReqDTO.getProjectCode());
        bmsProjectDict.setProjectName(bmsProjectAddReqDTO.getProjectName());
        bmsProjectDict.setKdProjectCode(bmsProjectAddReqDTO.getKdProjectCode());
        bmsProjectDict.setKdProjectName(bmsProjectAddReqDTO.getKdProjectName());
        bmsProjectDict.setKdProjectType(bmsProjectAddReqDTO.getKdProjectType());
        bmsProjectDict.setCreateTime(new Date());
        bmsProjectDict.setCreateUserId(SecurityContextHolder.getUserId());
        bmsProjectDict.setCreateUserName(SecurityContextHolder.getNickName());
        try {
            bmsProjectDictMapper.insert(bmsProjectDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("项目名称或者编号已经存在");
        }


    }

    @Override
    public void edit(BmsProjectEditReqDTO bmsProjectEditReqDTO) {
        BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectById(bmsProjectEditReqDTO.getId());
        if (bmsProjectDict == null) {
            throw new BusinessException("找不到此项目信息");
        }
        bmsProjectDict.setProjectName(bmsProjectEditReqDTO.getProjectName());
        bmsProjectDict.setKdProjectName(bmsProjectEditReqDTO.getKdProjectName());
        bmsProjectDict.setKdProjectCode(bmsProjectEditReqDTO.getKdProjectCode());
        bmsProjectDict.setKdProjectType(bmsProjectDict.getKdProjectType());
        try {
            bmsProjectDictMapper.updateById(bmsProjectDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("项目名称已经存在");
        }


    }

    @Override
    public void delete(Integer id) {
        BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectById(id);
        if (bmsProjectDict == null) {
            throw new BusinessException("找不到此项目信息");
        }
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectAllByProjectCode(bmsProjectDict.getProjectCode());
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            throw new BusinessException("此项目已经使用,无法删除");
        }
        bmsProjectDictMapper.deleteById(id);

    }
}
