package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.kd.KdTaskExecuteService;
import com.bio.drqi.bsm.req.BmsStockAddReqDTO;
import com.bio.drqi.bsm.req.BmsStockEditReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockQueryByUnitRspDTO;
import com.bio.drqi.bsm.service.BmsStockService;
import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
import com.bio.drqi.mapper.BmsStockDictMapper;
import com.bio.drqi.mapper.BmsStockLocationDictMapper;
import lombok.Data;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Data
public class BmsStockServiceImpl implements BmsStockService {

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private BmsStockLocationDictMapper bmsStockLocationDictMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private KdTaskExecuteService kdTaskExecuteService;


    @Override
    public List<BmsStockQueryByUnitRspDTO> queryStockByUnit(String unitCode) {
        List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectAllByUnitCodeOrderByIdDesc(unitCode);
        return BeanUtils.copyListProperties(bmsStockDictList, BmsStockQueryByUnitRspDTO.class);
    }

    @Override
    public void add(BmsStockAddReqDTO bmsStockAddReqDTO) {
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockNameAndUnitCode(bmsStockAddReqDTO.getStockName(), bmsStockAddReqDTO.getUnitCode());
        if (bmsStockDict != null) {
            throw new BusinessException("已经有此库房");
        }
        bmsStockDict = new BmsStockDict();
        bmsStockDict.setStockName(bmsStockAddReqDTO.getStockName());
        bmsStockDict.setStockCode(IdUtils.simpleUUID());
        bmsStockDict.setUnitCode(bmsStockAddReqDTO.getUnitCode());
        bmsStockDict.setKdNumber(null);
        bmsStockDict.setCreateTime(new Date());
        bmsStockDict.setCreateUserId(SecurityContextHolder.getUserId());
        bmsStockDict.setCreateUserName(SecurityContextHolder.getNickName());
        try {
            bmsStockDictMapper.insert(bmsStockDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("已经存在此名字库房");
        }

    }

    @Override
    public void edit(BmsStockEditReqDTO bmsStockEditReqDTO) {
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectById(bmsStockEditReqDTO.getId());
        if (bmsStockDict == null) {
            throw new BusinessException("不存在此库房");
        }
        bmsStockDict.setStockName(bmsStockEditReqDTO.getStockName());
        try {
            bmsStockDictMapper.updateById(bmsStockDict);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("已经存在此名字库房");
        }

    }

    @Override
    public void delete(Integer id) {
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectById(id);
        List<BmsProductStockTb> list = bmsProductStockTbMapper.selectAllByStockCode(bmsStockDict.getStockCode());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BusinessException("该库房已经使用，无法删除");
        }
        bmsStockDictMapper.deleteById(id);

    }

}
