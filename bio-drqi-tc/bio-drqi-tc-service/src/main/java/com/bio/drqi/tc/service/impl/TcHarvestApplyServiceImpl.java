package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.TcHarvestSeedApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.TcHarvestSeedApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHarvestApplyListPageReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcHarvestApplyListPageRspDTO;
import com.bio.drqi.tc.service.TcHarvestApplyService;
import com.bio.drqi.tc.service.dto.TcHarvestExcelDTO;
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
public class TcHarvestApplyServiceImpl implements TcHarvestApplyService {


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

    @Resource
    private BioDictMapper bioDictMapper;

    @Override
    public PageInfo<TcHarvestApplyListPageRspDTO> listPage(TcHarvestApplyListPageReqDTO tcHarvestApplyListPageReqDTO) {
        PageHelper.startPage(tcHarvestApplyListPageReqDTO.getPageNum(), tcHarvestApplyListPageReqDTO.getPageSize());
        List<TcHarvestSeedApplyTb> tcHarvestSeedApplyTbList = tcHarvestSeedApplyTbMapper.selectSelective(BeanUtils.copyProperties(tcHarvestApplyListPageReqDTO, TcHarvestSeedApplyTb.class));
        PageInfo<TcHarvestSeedApplyTb> srcPageInfo = new PageInfo<>(tcHarvestSeedApplyTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcHarvestApplyListPageRspDTO.class);
    }


    @Override
    public void createHarvestExcel(TcHarvestCreateHarvestExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectAllByExperimentNum(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum());
        if (CollectionUtil.isEmpty(tcPollinationTbList)) {
            throw new BusinessException("无授粉数据");
        }
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, String> codeOfNameMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        Map<String, String> harvestTypeNameMap = buildDictNameMap(BioDictTypeEnum.HARVEST_TYPE);

        List<TcHarvestExcelDTO> tcHarvestExcelDTOList = new ArrayList<>();
        for (TcPollinationTb tcPollinationTb : tcPollinationTbList) {
            TcHarvestExcelDTO tcHarvestExcelDTO = new TcHarvestExcelDTO();
            tcHarvestExcelDTO.setMotherRegionNum(tcPollinationTb.getMRegionNum());
            tcHarvestExcelDTO.setMotherSeedNum(tcPollinationTb.getMSeedNum());
            tcHarvestExcelDTO.setMotherSingleNumber(tcPollinationTb.getMSingleNumber());
            tcHarvestExcelDTO.setMotherSampleCode(tcPollinationTb.getMSampleCode());
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
            tcHarvestExcelDTO.setFatherVectorTaskCode(tcPollinationTb.getFVectorTaskCode());
            tcHarvestExcelDTO.setFatherGenerationName(tcPollinationTb.getFGenerationCode());
            tcHarvestExcelDTO.setFatherTcGene(tcPollinationTb.getFTcGene());
            tcHarvestExcelDTO.setHarvestTypeName(translateDict(harvestTypeNameMap, tcPollinationTb.getHarvestTypeCode()));
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

    private Map<String, String> buildDictNameMap(BioDictTypeEnum dictTypeEnum) {
        return bioDictMapper.selectAllByDictType(dictTypeEnum.name()).stream()
                .collect(Collectors.toMap(BioDict::getDictValueCode, BioDict::getDictValueName, (left, right) -> left));
    }

    private String translateDict(Map<String, String> dictNameMap, String dictValueCode) {
        if (StringUtils.isEmpty(dictValueCode)) {
            return "";
        }
        return dictNameMap.getOrDefault(dictValueCode, dictValueCode);
    }
}
