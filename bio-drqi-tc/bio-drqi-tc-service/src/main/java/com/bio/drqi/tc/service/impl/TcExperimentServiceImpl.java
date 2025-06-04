package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.mapper.TcSampleTestTbMapper;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.enums.SampleTestCheckResultEnum;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.req.TcExperimentQueryListExperimentDesignReqDTO;
import com.bio.drqi.tc.rsp.*;
import com.bio.drqi.tc.service.TcExperimentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Service
@Slf4j
public class TcExperimentServiceImpl implements TcExperimentService {

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;


    @Override
    public PageInfo<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO) {
        PageHelper.startPage(tcExperimentListPageReqDTO.getPageNum(), tcExperimentListPageReqDTO.getPageSize());
        TcExperimentTb tcExperimentTb = new TcExperimentTb();
        tcExperimentTb.setVectorTaskCodes(tcExperimentListPageReqDTO.getVectorTaskCode());
        tcExperimentTb.setProjectCodes(tcExperimentListPageReqDTO.getProjectCode());
        tcExperimentTb.setSpeciesCode(tcExperimentListPageReqDTO.getSpeciesCode());
        tcExperimentTb.setExperimentNum(tcExperimentListPageReqDTO.getExperimentNum());
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectSelective(tcExperimentTb);
        PageInfo<TcExperimentTb> srcPageInfo = new PageInfo<>(tcExperimentTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcExperimentListPageRspDTO.class);
    }

    @Override
    public List<TcExperimentListAllRspDTO> listAll() {
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectAllByExperimentStatusOrderByIdDesc(ExperimentStatusEnum.INIT.status);
        return BeanUtils.copyListProperties(tcExperimentTbList, TcExperimentListAllRspDTO.class);
    }

    @Override
    public List<TcExperimentListNoHarvestRspDTO> listNoHarvest() {
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectAllByExperimentStatusAndHarvestApplyNumIsNullOrderByIdDesc(ExperimentStatusEnum.INIT.status);
        return BeanUtils.copyListProperties(tcExperimentTbList, TcExperimentListNoHarvestRspDTO.class);
    }

    @Override
    public List<TcExperimentQueryListExperimentDesignRspDTO> queryListExperimentDesign(TcExperimentQueryListExperimentDesignReqDTO tcExperimentQueryListExperimentDesignReqDTO) {
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentNum(tcExperimentQueryListExperimentDesignReqDTO.getExperimentNum());
        if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
            List<TcExperimentQueryListExperimentDesignRspDTO> result = BeanUtils.copyListProperties(tcExperimentDesignTbList, TcExperimentQueryListExperimentDesignRspDTO.class);
            if (StringUtils.isNotEmpty(tcExperimentQueryListExperimentDesignReqDTO.getSampleApplyNum())) {
                result.forEach(obj -> {
                    List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNumAndSeedNumAndRegionNumAndCheckResult(tcExperimentQueryListExperimentDesignReqDTO.getSampleApplyNum(), obj.getSeedNum(), obj.getRegionNum(), SampleTestCheckResultEnum.stay.name());
                    obj.setStayNumber(StringUtils.isNotEmpty(tcSampleTestTbList) ? tcSampleTestTbList.size() : 0);
                });
            }
            return result;

        }

        return null;
    }


    @Override
    public List<TcExperimentListDetailRspDTO> listDetail(String experimentNum) {
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentNum(experimentNum);
        return BeanUtils.copyListProperties(tcExperimentDesignTbList, TcExperimentListDetailRspDTO.class);
    }

    @Override
    public void complete(Integer id) {
        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectById(id);
        if (tcExperimentTb == null) {
            throw new BusinessException("找不到试验");
        }
        tcExperimentTb.setExperimentStatus(ExperimentStatusEnum.OVER.status);
        tcExperimentTbMapper.updateById(tcExperimentTb);

    }

    @Override
    public void stop(Integer id) {
        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectById(id);
        if (tcExperimentTb == null) {
            throw new BusinessException("找不到试验");
        }
        if(!ExperimentStatusEnum.INIT.status.equals(tcExperimentTb.getExperimentStatus())){
            throw new BusinessException("只有进行中项目可以暂停");
        }
        tcExperimentTb.setExperimentStatus(ExperimentStatusEnum.STOP.status);
        tcExperimentTbMapper.updateById(tcExperimentTb);
    }

    @Override
    public void start(Integer id) {
        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectById(id);
        if (tcExperimentTb == null) {
            throw new BusinessException("找不到试验");
        }
        if(!ExperimentStatusEnum.STOP.status.equals(tcExperimentTb.getExperimentStatus())){
            throw new BusinessException("只有暂停中项目可以再次启用");
        }
        tcExperimentTb.setExperimentStatus(ExperimentStatusEnum.INIT.status);
        tcExperimentTbMapper.updateById(tcExperimentTb);
    }


}
