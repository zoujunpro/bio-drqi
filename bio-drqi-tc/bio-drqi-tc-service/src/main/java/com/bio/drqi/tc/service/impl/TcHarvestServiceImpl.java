package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.TcHarvestSeedTb;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.TcHarvestSeedTbMapper;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHavestDownSeedStockInExcelReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.service.TcHarvestService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
    private TcHarvestSeedTbMapper tcHarvestSeedTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private BioDictMapper bioDictMapper;


    @Override
    public PageInfo<TcHarvestListPageDetailRspDTO> listPage(TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO) {
        PageHelper.startPage(tcHarvestListPageDetailReqDTO.getPageNum(), tcHarvestListPageDetailReqDTO.getPageSize());
        List<TcHarvestSeedTb> tcHarvestSeedTbList = tcHarvestSeedTbMapper.selectSelective(BeanUtils.copyProperties(tcHarvestListPageDetailReqDTO, TcHarvestSeedTb.class));
        PageInfo<TcHarvestSeedTb> srcPageInfo = new PageInfo<>(tcHarvestSeedTbList);
        PageInfo<TcHarvestListPageDetailRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TcHarvestListPageDetailRspDTO.class);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, String> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        Map<String, String> pollinationMethodNameMap = buildDictNameMap(BioDictTypeEnum.POLLINATE_TYPE);
        Map<String, String> harvestTypeNameMap = buildDictNameMap(BioDictTypeEnum.HARVEST_TYPE);
        if (CollectionUtil.isNotEmpty(resultPageInfo.getList())) {
            resultPageInfo.getList().forEach(tcPollinationListPageDetailRspDTO -> {
                tcPollinationListPageDetailRspDTO.setFBreedName(codeNameCerBreedDictMap.get(tcPollinationListPageDetailRspDTO.getFBreedCode()));
                tcPollinationListPageDetailRspDTO.setMBreedName(codeNameCerBreedDictMap.get(tcPollinationListPageDetailRspDTO.getMBreedCode()));
                tcPollinationListPageDetailRspDTO.setPollinationMethodName(translateDict(pollinationMethodNameMap, tcPollinationListPageDetailRspDTO.getPollinationMethodCode()));
                tcPollinationListPageDetailRspDTO.setHarvestTypeName(translateDict(harvestTypeNameMap, tcPollinationListPageDetailRspDTO.getHarvestTypeCode()));
            });
        }
        return resultPageInfo;
    }

    @Override
    public void downSeedStockInExcel(TcHavestDownSeedStockInExcelReqDTO tcHavestDownSeedStockInExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<TcHarvestSeedTb> tcHarvestSeedTbList = tcHarvestSeedTbMapper.selectBatchIds(tcHavestDownSeedStockInExcelReqDTO.getIdList());
        List<com.bio.drqi.common.dto.SeedInStockExcelDTO> seedInStockExcelDTOList = new ArrayList<>();
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        Map<String, CerBreedDict> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, cerBreedDict -> cerBreedDict));
        Map<String, String> codeNameCerSpeciesDictMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        Map<String, String> pollinationMethodNameMap = buildDictNameMap(BioDictTypeEnum.POLLINATE_TYPE);
        Map<String, String> harvestTypeNameMap = buildDictNameMap(BioDictTypeEnum.HARVEST_TYPE);
        Map<String, String> materialTypeNameMap = buildDictNameMap(BioDictTypeEnum.MATERIAL_TYPE);
        if (CollectionUtil.isNotEmpty(tcHarvestSeedTbList)) {
            for (TcHarvestSeedTb tcHarvestSeedTb : tcHarvestSeedTbList) {
                com.bio.drqi.common.dto.SeedInStockExcelDTO seedInStockExcelDTO = new com.bio.drqi.common.dto.SeedInStockExcelDTO();
                seedInStockExcelDTO.setSource(SeedSourceEnum.CODE_4.name);

                seedInStockExcelDTO.setGeneration(StringUtils.isNotEmpty(tcHarvestSeedTb.getMGenerationCode()) ? GenerationEnum.nextGenerationCode(tcHarvestSeedTb.getMGenerationCode()) : null);
                seedInStockExcelDTO.setPlantCode(null);
                seedInStockExcelDTO.setMaterialTypeName(translateDict(materialTypeNameMap, tcHarvestSeedTb.getMaterialType()));
                seedInStockExcelDTO.setVectorTaskCode(tcHarvestSeedTb.getFVectorTaskCode());
                seedInStockExcelDTO.setExperimentNum(tcHarvestSeedTb.getExperimentNum());
                seedInStockExcelDTO.setFatherRegionNum(tcHarvestSeedTb.getFRegionNum());
                seedInStockExcelDTO.setMatherRegionNum(tcHarvestSeedTb.getMRegionNum());
                seedInStockExcelDTO.setFatherSingleNum(tcHarvestSeedTb.getFSingleNumber());
                seedInStockExcelDTO.setMatherSingleNum(tcHarvestSeedTb.getMSingleNumber());
                seedInStockExcelDTO.setProductionLocationName("武清大田");
                seedInStockExcelDTO.setMatherInfo(null);
                seedInStockExcelDTO.setFatherInfo(null);
                seedInStockExcelDTO.setMatherSeedNum(tcHarvestSeedTb.getMSeedNum());
                seedInStockExcelDTO.setFatherSeedNum(tcHarvestSeedTb.getFSeedNum());
                if (StringUtils.isNotEmpty(tcHarvestSeedTb.getMBreedCode())) {
                    CerBreedDict cerBreedDict = codeNameCerBreedDictMap.get(tcHarvestSeedTb.getMBreedCode());
                    if (cerBreedDict != null) {
                        seedInStockExcelDTO.setBreedName(cerBreedDict.getBreedName());
                        seedInStockExcelDTO.setSpeciesName(codeNameCerSpeciesDictMap.get(cerBreedDict.getSpeciesCode()));
                    }
                }
                seedInStockExcelDTO.setHarvestTypeName(translateDict(harvestTypeNameMap, tcHarvestSeedTb.getHarvestTypeCode()));
                seedInStockExcelDTO.setHarvestTime(tcHarvestSeedTb.getHarvestTime());
                seedInStockExcelDTO.setPollinationMethodName(translateDict(pollinationMethodNameMap, tcHarvestSeedTb.getPollinationMethodCode()));
                seedInStockExcelDTO.setSeedNumber(tcHarvestSeedTb.getSeedNumber());
                seedInStockExcelDTO.setUnit(tcHarvestSeedTb.getUnit());
                seedInStockExcelDTO.setAliasName(null);
                seedInStockExcelDTO.setRemarks(tcHarvestSeedTb.getRemark());
                seedInStockExcelDTOList.add(seedInStockExcelDTO);
            }

        }
        ExcelUtil.writeExcel("种子入库数据", "sheet1", seedInStockExcelDTOList, com.bio.drqi.common.dto.SeedInStockExcelDTO.class, httpServletResponse);
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
