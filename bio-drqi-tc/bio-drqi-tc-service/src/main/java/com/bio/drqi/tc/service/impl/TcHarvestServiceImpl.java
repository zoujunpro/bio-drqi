package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.TcHarvestSeedApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.TcHarvestSeedApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageRspDTO;
import com.bio.drqi.tc.service.TcHarvestService;
import com.bio.drqi.tc.service.dto.TcHarvestExcelDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
public class TcHarvestServiceImpl implements TcHarvestService {


    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;


    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private TcHarvestSeedApplyTbMapper tcHarvestSeedApplyTbMapper;

    @Resource
    private OssService ossService;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public PageInfo<TcHarvestListPageRspDTO> listPage(TcHarvestListPageReqDTO tcHarvestListPageReqDTO) {
        PageHelper.startPage(tcHarvestListPageReqDTO.getPageNum(), tcHarvestListPageReqDTO.getPageSize());
        List<TcHarvestSeedApplyTb> tcHarvestSeedApplyTbList = tcHarvestSeedApplyTbMapper.selectSelective(BeanUtils.copyProperties(tcHarvestListPageReqDTO, TcHarvestSeedApplyTb.class));
        PageInfo<TcHarvestSeedApplyTb> srcPageInfo = new PageInfo<>(tcHarvestSeedApplyTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcHarvestListPageRspDTO.class);
    }

    @Override
    public PageInfo<TcHarvestListPageDetailRspDTO> listPageDetail(TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO) {
        PageHelper.startPage(tcHarvestListPageDetailReqDTO.getPageNum(), tcHarvestListPageDetailReqDTO.getPageSize());
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectSelective(BeanUtils.copyProperties(tcHarvestListPageDetailReqDTO, TcPollinationTb.class));
        PageInfo<TcPollinationTb> srcPageInfo = new PageInfo<>(tcPollinationTbList);
        PageInfo<TcHarvestListPageDetailRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TcHarvestListPageDetailRspDTO.class);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, String> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        if (CollectionUtil.isNotEmpty(resultPageInfo.getList())) {
            resultPageInfo.getList().forEach(tcPollinationListPageDetailRspDTO -> {
                tcPollinationListPageDetailRspDTO.setFBreedName(codeNameCerBreedDictMap.get(tcPollinationListPageDetailRspDTO.getFBreedCode()));
                tcPollinationListPageDetailRspDTO.setMBreedName(codeNameCerBreedDictMap.get(tcPollinationListPageDetailRspDTO.getFBreedCode()));
            });
        }
        return resultPageInfo;
    }

    @Override
    public void createHarvestExcel(TcHarvestCreateHarvestExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectAllByExperimentNum(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum());
        if (CollectionUtil.isEmpty(tcPollinationTbList)) {
            throw new BusinessException("无授粉数据");
        }
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, String> codeOfNameMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));

        List<TcHarvestExcelDTO> tcHarvestExcelDTOList = new ArrayList<>();
        for (TcPollinationTb tcPollinationTb : tcPollinationTbList) {
            TcHarvestExcelDTO tcHarvestExcelDTO = new TcHarvestExcelDTO();
            tcHarvestExcelDTO.setMotherRegionNum(tcPollinationTb.getMRegionNum());
            tcHarvestExcelDTO.setMotherSeedNum(tcPollinationTb.getMSeedNum());
            tcHarvestExcelDTO.setMotherSingleNumber(tcPollinationTb.getMSingleNumber());
            tcHarvestExcelDTO.setMotherSampleCode(tcPollinationTb.getMSampleCode());
            tcHarvestExcelDTO.setMotherTcSampleCode(tcPollinationTb.getMTcSampleCode());
            if (tcPollinationTb.getMBreedCode() != null) {
                tcHarvestExcelDTO.setMotherBreedName(codeOfNameMap.get(tcPollinationTb.getMBreedCode()));
            }
            if (tcPollinationTb.getFBreedCode() != null) {
                tcHarvestExcelDTO.setFatherBreedName(codeOfNameMap.get(tcPollinationTb.getFBreedCode()));
            }
            tcHarvestExcelDTO.setMotherVectorTaskCode(tcPollinationTb.getMVectorTaskCode());
            tcHarvestExcelDTO.setMotherGenerationName(tcPollinationTb.getMGenerationCode());
            tcHarvestExcelDTO.setMotherTcGene(tcPollinationTb.getMTcGene());
            tcHarvestExcelDTO.setFatherRegionNum(tcPollinationTb.getFRegionNum());
            tcHarvestExcelDTO.setFatherSeedNum(tcPollinationTb.getFSeedNum());
            tcHarvestExcelDTO.setFatherSingleNumber(tcPollinationTb.getFSingleNumber());
            tcHarvestExcelDTO.setFatherSampleCode(tcPollinationTb.getFSampleCode());
            tcHarvestExcelDTO.setFatherTcSampleCode(tcPollinationTb.getFTcSampleCode());
            tcHarvestExcelDTO.setFatherVectorTaskCode(tcPollinationTb.getFVectorTaskCode());
            tcHarvestExcelDTO.setFatherGenerationName(tcPollinationTb.getFGenerationCode());
            tcHarvestExcelDTO.setFatherTcGene(tcPollinationTb.getFTcGene());
            tcHarvestExcelDTO.setHarvestTypeName(tcPollinationTb.getHarvestTypeName());
            tcHarvestExcelDTOList.add(tcHarvestExcelDTO);
        }
        String excelTemplateName = "田测收获模板表V1.0.xlsx";
        String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
        try {
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
            ExcelUtil.fillExcel(templateDir, tcHarvestExcelDTOList, TcHarvestExcelDTO.class, httpServletResponse);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }
    }

}
