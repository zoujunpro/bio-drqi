package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.enums.TcDesignTypeEnum;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.enums.SampleTestCheckResultEnum;
import com.bio.drqi.tc.req.TcExperimentApplyListPageReqDTO;
import com.bio.drqi.tc.req.TcExperimentQueryByPdAndVectorTaskCodeReqDTO;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.*;
import com.bio.drqi.tc.service.TcExperimentApplyService;
import com.bio.drqi.tc.service.dto.EvaluationExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.dto.HybridExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.dto.SurvivalCompetitionExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.excel.ExcelSelectedWriteHandler;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcExperimentApplyServiceImpl implements TcExperimentApplyService {

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

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;


    @Override
    public PageInfo<TcExperimentApplyListPageRspDTO> listPage(TcExperimentApplyListPageReqDTO tcExperimentApplyListPageReqDTO) {

        List<TcHarvestSeedApplyTb> tcHarvestSeedApplyTbList = tcHarvestSeedApplyTbMapper.selectSelective(null);
        List<TcPollinationApplyTb> tcPollinationApplyTbList = tcPollinationApplyTbMapper.selectSelective(null);
        Map<String, String> seedProduceAddressDictMap = seedProduceAddressDictMapper.selectAll().stream().collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName));
        TcExperimentTb tcExperimentTb = new TcExperimentTb();
        tcExperimentTb.setVectorTaskCodes(tcExperimentApplyListPageReqDTO.getVectorTaskCode());
        tcExperimentTb.setSpeciesCode(tcExperimentApplyListPageReqDTO.getSpeciesCode());
        tcExperimentTb.setExperimentNum(tcExperimentApplyListPageReqDTO.getExperimentNum());
        PageHelper.startPage(tcExperimentApplyListPageReqDTO.getPageNum(), tcExperimentApplyListPageReqDTO.getPageSize());
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectSelective(tcExperimentTb);
        PageInfo<TcExperimentTb> srcPageInfo = new PageInfo<>(tcExperimentTbList);
        PageInfo<TcExperimentApplyListPageRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TcExperimentApplyListPageRspDTO.class);
        List<String> harvestExperimentNumList = tcHarvestSeedApplyTbList.stream().map(TcHarvestSeedApplyTb::getExperimentNum).distinct().collect(Collectors.toList());
        List<String> pollinationExperimentNumList = tcPollinationApplyTbList.stream().map(TcPollinationApplyTb::getExperimentNum).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(resultPageInfo.getList())) {
            resultPageInfo.getList().forEach(tcExperimentApplyListPageRspDTO -> {
                tcExperimentApplyListPageRspDTO.setHarvestFlag(harvestExperimentNumList.contains(tcExperimentApplyListPageRspDTO.getExperimentNum()) ? BioDrQiContents.Y : BioDrQiContents.N);
                tcExperimentApplyListPageRspDTO.setPollinationFlag(pollinationExperimentNumList.contains(tcExperimentApplyListPageRspDTO.getExperimentNum()) ? BioDrQiContents.Y : BioDrQiContents.N);
                tcExperimentApplyListPageRspDTO.setExperimentAddressName(seedProduceAddressDictMap.get(tcExperimentApplyListPageRspDTO.getExperimentAddressCode()));
            });
        }
        return resultPageInfo;
    }

    @Override
    public List<TcExperimentApplyQueryByPdAndVectorTaskCodeRspDTO> queryByPdAndVectorTaskCode(TcExperimentQueryByPdAndVectorTaskCodeReqDTO tcExperimentQueryByPdAndVectorTaskCodeReqDTO) {
        List<TcHarvestSeedApplyTb> tcHarvestSeedApplyTbList = tcHarvestSeedApplyTbMapper.selectSelective(null);
        List<TcPollinationApplyTb> tcPollinationApplyTbList = tcPollinationApplyTbMapper.selectSelective(null);
        Map<String, String> seedProduceAddressDictMap = seedProduceAddressDictMapper.selectAll().stream().collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName));

        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByVectorTaskCodeAndPdImplementCode(tcExperimentQueryByPdAndVectorTaskCodeReqDTO.getVectorTaskCode(), tcExperimentQueryByPdAndVectorTaskCodeReqDTO.getPdImplementCode());
        if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
            List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectAllByExperimentNumInAndExperimentTypeLike(tcExperimentDesignTbList.stream().map(TcExperimentDesignTb::getExperimentNum).distinct().collect(Collectors.toList()),tcExperimentQueryByPdAndVectorTaskCodeReqDTO.getExperimentType());
            List<TcExperimentApplyQueryByPdAndVectorTaskCodeRspDTO> tcExperimentApplyQueryByPdAndVectorTaskCodeRspDTOList = BeanUtils.copyListProperties(tcExperimentTbList, TcExperimentApplyQueryByPdAndVectorTaskCodeRspDTO.class);
            List<String> harvestExperimentNumList = tcHarvestSeedApplyTbList.stream().map(TcHarvestSeedApplyTb::getExperimentNum).distinct().collect(Collectors.toList());
            List<String> pollinationExperimentNumList = tcPollinationApplyTbList.stream().map(TcPollinationApplyTb::getExperimentNum).distinct().collect(Collectors.toList());
            tcExperimentApplyQueryByPdAndVectorTaskCodeRspDTOList.forEach(tcExperimentListPageRspDTO -> {
                tcExperimentListPageRspDTO.setHarvestFlag(harvestExperimentNumList.contains(tcExperimentListPageRspDTO.getExperimentNum()) ? BioDrQiContents.Y : BioDrQiContents.N);
                tcExperimentListPageRspDTO.setPollinationFlag(pollinationExperimentNumList.contains(tcExperimentListPageRspDTO.getExperimentNum()) ? BioDrQiContents.Y : BioDrQiContents.N);
                tcExperimentListPageRspDTO.setExperimentAddressName(seedProduceAddressDictMap.get(tcExperimentListPageRspDTO.getExperimentAddressCode()));
            });
            return tcExperimentApplyQueryByPdAndVectorTaskCodeRspDTOList;
        }
        return null;
    }

    @Override
    public List<TcExperimentApplyListAllRspDTO> listAll() {
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectAllByExperimentStatusOrderByIdDesc(ExperimentStatusEnum.INIT.status);
        Map<String, String> seedProduceAddressDictMap = seedProduceAddressDictMapper.selectAll().stream().collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName));
        List<TcExperimentApplyListAllRspDTO> tcExperimentListAllRspDTOApplyList = BeanUtils.copyListProperties(tcExperimentTbList, TcExperimentApplyListAllRspDTO.class);
        tcExperimentListAllRspDTOApplyList.forEach(tcExperimentApplyListAllRspDTO -> {
            tcExperimentApplyListAllRspDTO.setExperimentAddressName(seedProduceAddressDictMap.get(tcExperimentApplyListAllRspDTO.getExperimentAddressCode()));
        });
        return tcExperimentListAllRspDTOApplyList;
    }


    @Override
    public void downTemplate(String designType, HttpServletResponse httpServletResponse) {
        TcDesignTypeEnum designTypeEnum = TcDesignTypeEnum.getByName(designType);
        if (designTypeEnum == null) {
            throw new BusinessException("田间设计类型填写错误");
        }
        Class<?> headClass = getExperimentDesignExcelClass(designTypeEnum);
        String fileName = "田间设计方案模板-" + designTypeEnum.name;
        try {
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setCharacterEncoding("utf-8");
            httpServletResponse.setHeader("Content-disposition", "attachment;filename="
                    + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            ExcelWriter excelWriter = EasyExcel.write(httpServletResponse.getOutputStream())
                    .registerWriteHandler(new ExcelSelectedWriteHandler(headClass))
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet(0, designTypeEnum.name)
                    .head(headClass).build();
            excelWriter.write(Collections.emptyList(), writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            log.error("【田间设计方案模板】下载失败", e);
            throw new BusinessException("文件处理异常");
        }
    }

    private Class<?> getExperimentDesignExcelClass(TcDesignTypeEnum designTypeEnum) {
        switch (designTypeEnum) {
            case SURVIVAL_COMPETITION:
                return SurvivalCompetitionExperimentDesignExcelDTO.class;
            case EVALUATION:
                return EvaluationExperimentDesignExcelDTO.class;
            case HYBRID:
                return HybridExperimentDesignExcelDTO.class;
            default:
                throw new BusinessException("田间设计类型填写错误");
        }
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
