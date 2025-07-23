package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcExperimentServiceImpl implements TcExperimentService {

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcHarvestSeedApplyTbMapper tcHarvestSeedApplyTbMapper;

    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;


    @Override
    public PageInfo<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO) {
        PageHelper.startPage(tcExperimentListPageReqDTO.getPageNum(), tcExperimentListPageReqDTO.getPageSize());
        List<TcHarvestSeedApplyTb> tcHarvestSeedApplyTbList = tcHarvestSeedApplyTbMapper.selectSelective(null);
        List<TcPollinationApplyTb> tcPollinationApplyTbList = tcPollinationApplyTbMapper.selectSelective(null);
        TcExperimentTb tcExperimentTb = new TcExperimentTb();
        tcExperimentTb.setVectorTaskCodes(tcExperimentListPageReqDTO.getVectorTaskCode());
        tcExperimentTb.setProjectCodes(tcExperimentListPageReqDTO.getProjectCode());
        tcExperimentTb.setSpeciesCode(tcExperimentListPageReqDTO.getSpeciesCode());
        tcExperimentTb.setExperimentNum(tcExperimentListPageReqDTO.getExperimentNum());
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectSelective(tcExperimentTb);
        PageInfo<TcExperimentTb> srcPageInfo = new PageInfo<>(tcExperimentTbList);
        PageInfo<TcExperimentListPageRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TcExperimentListPageRspDTO.class);
        List<String> harvestExperimentNumList = tcHarvestSeedApplyTbList.stream().map(TcHarvestSeedApplyTb::getExperimentNum).distinct().collect(Collectors.toList());
        List<String> pollinationExperimentNumList = tcPollinationApplyTbList.stream().map(TcPollinationApplyTb::getExperimentNum).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(resultPageInfo.getList())) {
            resultPageInfo.getList().forEach(tcExperimentListPageRspDTO -> {
                tcExperimentListPageRspDTO.setHarvestFlag(harvestExperimentNumList.contains(tcExperimentListPageRspDTO.getExperimentNum()) ? BioDrQiContents.Y : BioDrQiContents.N);
                tcExperimentListPageRspDTO.setPollinationFlag(pollinationExperimentNumList.contains(tcExperimentListPageRspDTO.getExperimentNum()) ? BioDrQiContents.Y : BioDrQiContents.N);
            });
        }
        return resultPageInfo;
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
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        Map<String, String> breedCodeOfNameMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        Map<String, String> speciesCodeOfNameMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
            List<TcExperimentQueryListExperimentDesignRspDTO> result = BeanUtils.copyListProperties(tcExperimentDesignTbList, TcExperimentQueryListExperimentDesignRspDTO.class);
            if (StringUtils.isNotEmpty(tcExperimentQueryListExperimentDesignReqDTO.getSampleApplyNum())) {
                result.forEach(obj -> {
                    List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNumAndSeedNumAndRegionNumAndCheckResult(tcExperimentQueryListExperimentDesignReqDTO.getSampleApplyNum(), obj.getSeedNum(), obj.getRegionNum(), SampleTestCheckResultEnum.stay.name());
                    obj.setStayNumber(StringUtils.isNotEmpty(tcSampleTestTbList) ? tcSampleTestTbList.size() : 0);
                    obj.setSpeciesName(speciesCodeOfNameMap.get(obj.getSpeciesCode()));
                    obj.setBreedName(breedCodeOfNameMap.get(obj.getBreedCode()));
                });
            }
            return result;

        }

        return null;
    }


    @Override
    public List<TcExperimentListDetailRspDTO> listDetail(String experimentNum) {
        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(experimentNum);
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentNum(experimentNum);
        List<TcExperimentListDetailRspDTO> rspDTOList = BeanUtils.copyListProperties(tcExperimentDesignTbList, TcExperimentListDetailRspDTO.class);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(tcExperimentTb.getSpeciesCode());

        Map<String, String> codeNameMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        rspDTOList.forEach(tcExperimentListDetailRspDTO -> {
            tcExperimentListDetailRspDTO.setBreedName(codeNameMap.get(tcExperimentListDetailRspDTO.getBreedCode()));
        });
        return rspDTOList;
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
        if (!ExperimentStatusEnum.INIT.status.equals(tcExperimentTb.getExperimentStatus())) {
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
        if (!ExperimentStatusEnum.STOP.status.equals(tcExperimentTb.getExperimentStatus())) {
            throw new BusinessException("只有暂停中项目可以再次启用");
        }
        tcExperimentTb.setExperimentStatus(ExperimentStatusEnum.INIT.status);
        tcExperimentTbMapper.updateById(tcExperimentTb);
    }


}
