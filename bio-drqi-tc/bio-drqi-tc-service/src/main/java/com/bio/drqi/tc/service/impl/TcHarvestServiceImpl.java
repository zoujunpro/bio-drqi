package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
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
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;


    @Override
    public PageInfo<TcHarvestListPageDetailRspDTO> listPage(TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO) {
        PageHelper.startPage(tcHarvestListPageDetailReqDTO.getPageNum(), tcHarvestListPageDetailReqDTO.getPageSize());
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectSelective(BeanUtils.copyProperties(tcHarvestListPageDetailReqDTO, TcPollinationTb.class));
        PageInfo<TcPollinationTb> srcPageInfo = new PageInfo<>(tcPollinationTbList);
        PageInfo<TcHarvestListPageDetailRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TcHarvestListPageDetailRspDTO.class);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
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
    public void downSeedStockInExcel(TcHavestDownSeedStockInExcelReqDTO tcHavestDownSeedStockInExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectBatchIds(tcHavestDownSeedStockInExcelReqDTO.getIdList());
        List<com.bio.drqi.common.dto.SeedInStockExcelDTO> seedInStockExcelDTOList = new ArrayList<>();
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        Map<String, CerBreedDict> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, cerBreedDict -> cerBreedDict));
        Map<String, String> codeNameCerSpeciesDictMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        if (CollectionUtil.isNotEmpty(tcPollinationTbList)) {
            for (TcPollinationTb tcPollinationTb : tcPollinationTbList) {
                com.bio.drqi.common.dto.SeedInStockExcelDTO seedInStockExcelDTO = new com.bio.drqi.common.dto.SeedInStockExcelDTO();
                seedInStockExcelDTO.setSource(SeedSourceEnum.CODE_4.name);
                seedInStockExcelDTO.setGeneration(null);
                seedInStockExcelDTO.setPlantCode(null);
                seedInStockExcelDTO.setVectorTaskCode(null);
                seedInStockExcelDTO.setMaterialTypeName(null);
                seedInStockExcelDTO.setExperimentNum(tcPollinationTb.getExperimentNum());
                seedInStockExcelDTO.setFatherRegionNum(tcPollinationTb.getFRegionNum());
                seedInStockExcelDTO.setMatherRegionNum(tcPollinationTb.getFRegionNum());
                seedInStockExcelDTO.setFatherSingleNum(tcPollinationTb.getFSingleNumber());
                seedInStockExcelDTO.setMatherSingleNum(tcPollinationTb.getMSingleNumber());
                seedInStockExcelDTO.setProductionLocationName("武清大田");
                seedInStockExcelDTO.setMatherInfo(null);
                seedInStockExcelDTO.setFatherInfo(null);
                seedInStockExcelDTO.setMatherSeedNum(tcPollinationTb.getFSeedNum());
                seedInStockExcelDTO.setFatherSeedNum(tcPollinationTb.getFSeedNum());
                if(StringUtils.isNotEmpty(tcPollinationTb.getMBreedCode())){
                    seedInStockExcelDTO.setSpeciesName(codeNameCerSpeciesDictMap.get(codeNameCerBreedDictMap.get(tcPollinationTb.getMBreedCode()).getBreedCode()));
                    seedInStockExcelDTO.setBreedName(codeNameCerBreedDictMap.get(tcPollinationTb.getMBreedCode()).getBreedName());
                }
                seedInStockExcelDTO.setHarvestTypeName(tcPollinationTb.getHarvestTypeName());
                seedInStockExcelDTO.setHarvestTime(tcPollinationTb.getHarvestTime());
                seedInStockExcelDTO.setPollinationMethodName(tcPollinationTb.getPollinationMethodName());
                seedInStockExcelDTO.setSeedNumber(null);
                seedInStockExcelDTO.setUnit(null);
                seedInStockExcelDTO.setAliasName(null);
                seedInStockExcelDTO.setRemarks(null);
                seedInStockExcelDTOList.add(seedInStockExcelDTO);
            }

        }
        ExcelUtil.writeExcel("种子入库数据", "sheet1", seedInStockExcelDTOList, com.bio.drqi.common.dto.SeedInStockExcelDTO.class, httpServletResponse);
    }

}
