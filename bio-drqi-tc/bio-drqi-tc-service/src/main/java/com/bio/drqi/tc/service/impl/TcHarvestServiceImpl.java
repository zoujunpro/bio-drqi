package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.TcHarvestSeedApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.TcHarvestSeedApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.req.TcHarvestApplyListPageReqDTO;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHavestDownSeedStockInExcelReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestApplyListPageRspDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.service.TcHarvestApplyService;
import com.bio.drqi.tc.service.TcHarvestService;
import com.bio.drqi.tc.service.dto.TcHarvestExcelDTO;
import com.bio.drqi.tc.service.dto.TcSeedInStockExcelDTO;
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
        List<TcSeedInStockExcelDTO> tcSeedInStockExcelDTOList = new ArrayList<>();
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        Map<String, CerBreedDict> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, cerBreedDict -> cerBreedDict));
        Map<String, String> codeNameCerSpeciesDictMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        if (CollectionUtil.isNotEmpty(tcPollinationTbList)) {
            for (TcPollinationTb tcPollinationTb : tcPollinationTbList) {
                TcSeedInStockExcelDTO tcSeedInStockExcelDTO = new TcSeedInStockExcelDTO();
                tcSeedInStockExcelDTO.setSource(SeedSourceEnum.CODE_4.name);
                tcSeedInStockExcelDTO.setGeneration(null);
                tcSeedInStockExcelDTO.setPlantCode(null);
                tcSeedInStockExcelDTO.setVectorTaskCode(null);
                tcSeedInStockExcelDTO.setMaterialTypeName(null);
                tcSeedInStockExcelDTO.setExperimentNum(tcPollinationTb.getExperimentNum());
                tcSeedInStockExcelDTO.setFatherRegionNum(tcPollinationTb.getFRegionNum());
                tcSeedInStockExcelDTO.setMatherRegionNum(tcPollinationTb.getFRegionNum());
                tcSeedInStockExcelDTO.setFatherSingleNum(tcPollinationTb.getFSingleNumber());
                tcSeedInStockExcelDTO.setMatherSingleNum(tcPollinationTb.getMSingleNumber());
                tcSeedInStockExcelDTO.setProductionLocationName("武清大田");
                tcSeedInStockExcelDTO.setMatherInfo(null);
                tcSeedInStockExcelDTO.setFatherInfo(null);
                tcSeedInStockExcelDTO.setMatherSeedNum(tcPollinationTb.getFSeedNum());
                tcSeedInStockExcelDTO.setFatherSeedNum(tcPollinationTb.getFSeedNum());
                tcSeedInStockExcelDTO.setSpeciesName(codeNameCerSpeciesDictMap.get(codeNameCerBreedDictMap.get(tcPollinationTb.getMBreedCode()).getBreedCode()));
                tcSeedInStockExcelDTO.setBreedName(codeNameCerBreedDictMap.get(tcPollinationTb.getMBreedCode()).getBreedName());
                tcSeedInStockExcelDTO.setHarvestTypeName(tcPollinationTb.getHarvestTypeName());
                tcSeedInStockExcelDTO.setHarvestTime(tcPollinationTb.getHarvestTime());
                tcSeedInStockExcelDTO.setPollinationMethodName(tcPollinationTb.getPollinationMethodName());
                tcSeedInStockExcelDTO.setSeedNumber(null);
                tcSeedInStockExcelDTO.setUnit(null);
                tcSeedInStockExcelDTO.setAliasName(null);
                tcSeedInStockExcelDTO.setRemarks(null);
                tcSeedInStockExcelDTOList.add(tcSeedInStockExcelDTO);
            }

        }
        ExcelUtil.writeExcel("种子入库数据", "sheet1", tcSeedInStockExcelDTOList, TcSeedInStockExcelDTO.class, httpServletResponse);
    }

}
