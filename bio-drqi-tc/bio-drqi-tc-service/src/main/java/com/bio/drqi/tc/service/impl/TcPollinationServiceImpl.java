package com.bio.drqi.tc.service.impl;

import java.util.Date;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.enums.PollinationParentFlagEnum;
import com.bio.drqi.tc.enums.SampleTestCheckResultEnum;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationExportPollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationCreatePollinationExcelRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPollinationApplyNumNotHarvestRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
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
public class TcPollinationServiceImpl implements TcPollinationService {


    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;

    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private OssService ossService;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private TcPollinationSingleNumTbMapper tcPollinationSingleNumTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Override
    public PageInfo<TcPollinationListPageRspDTO> listPage(TcPollinationListPageReqDTO tcPollinationListPageReqDTO) {
        PageHelper.startPage(tcPollinationListPageReqDTO.getPageNum(), tcPollinationListPageReqDTO.getPageSize());
        List<TcPollinationApplyTb> tcPollinationApplyTbList = tcPollinationApplyTbMapper.selectSelective(BeanUtils.copyProperties(tcPollinationListPageReqDTO, TcPollinationApplyTb.class));
        PageInfo<TcPollinationApplyTb> srcPageInfo = new PageInfo<>(tcPollinationApplyTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcPollinationListPageRspDTO.class);
    }

    @Override
    public List<TcPollinationListPollinationApplyNumNotHarvestRspDTO> listPollinationApplyNumNotHarvest() {
        List<TcPollinationListPollinationApplyNumNotHarvestRspDTO> tcPollinationListPollinationApplyNumNotHarvestRspDTOS = new ArrayList<>();
        List<TcPollinationApplyTb> tcPollinationApplyTbList = tcPollinationApplyTbMapper.selectAllByHarvestApplyNumIsNullOrderByIdDesc();
        for (TcPollinationApplyTb tcPollinationApplyTb : tcPollinationApplyTbList) {
            BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueCode(BioDictTypeEnum.POLLINATE_TYPE.name(), tcPollinationApplyTb.getPollinationType());

            TcPollinationListPollinationApplyNumNotHarvestRspDTO tcPollinationListPollinationApplyNumNotHarvestRspDTO = new TcPollinationListPollinationApplyNumNotHarvestRspDTO();
            tcPollinationListPollinationApplyNumNotHarvestRspDTO.setPollinationTypeName(bioDict == null ? "未知" : bioDict.getDictValueName());
            tcPollinationListPollinationApplyNumNotHarvestRspDTO.setPollinationApplyNum(tcPollinationApplyTb.getPollinationApplyNum());
            tcPollinationListPollinationApplyNumNotHarvestRspDTO.setCreateUserName(tcPollinationApplyTb.getCreateUserName());
            tcPollinationListPollinationApplyNumNotHarvestRspDTO.setCreateTime(tcPollinationApplyTb.getCreateTime());
            tcPollinationListPollinationApplyNumNotHarvestRspDTOS.add(tcPollinationListPollinationApplyNumNotHarvestRspDTO);
        }
        return tcPollinationListPollinationApplyNumNotHarvestRspDTOS;


    }

    @Override
    public PageInfo<TcPollinationListPageDetailRspDTO> listPageDetail(TcPollinationListPageDetailReqDTO tcPollinationListPageDetailReqDTO) {
        PageHelper.startPage(tcPollinationListPageDetailReqDTO.getPageNum(), tcPollinationListPageDetailReqDTO.getPageSize());
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectSelective(BeanUtils.copyProperties(tcPollinationListPageDetailReqDTO, TcPollinationTb.class));
        PageInfo<TcPollinationTb> srcPageInfo = new PageInfo<>(tcPollinationTbList);
        PageInfo<TcPollinationListPageDetailRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TcPollinationListPageDetailRspDTO.class);
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

    /**
     * 针对一个试验，如果有授粉中工单，则不能再次发起下载excel，必须等上一个授粉工单结束
     * 连续多次发起下载授粉excel下载，以最后一次授粉excel数据为准
     *
     * @param tcPollinationCreatePollinationExcelReqDTO
     * @return
     */
    @Override
    public List<TcPollinationExcelDTO> createPollinationExcel(TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO) {
        List<TcPollinationExcelDTO> matherList = new ArrayList<TcPollinationExcelDTO>();
        List<TcPollinationExcelDTO> fatherList = new ArrayList<TcPollinationExcelDTO>();
        List<TcPollinationSingleNumTb> currentTcPollinationSingleNumTbList = new ArrayList<TcPollinationSingleNumTb>();
        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum());
        if (tcExperimentTb == null) {
            throw new BusinessException("试验方案不存在");
        }
        if (!ExperimentStatusEnum.INIT.status.equals(tcExperimentTb.getExperimentStatus())) {
            throw new BusinessException("非进行中试验，无法进行任何操作");
        }
        if (tcExperimentTb.getHarvestApplyNum() != null) {
            throw new BusinessException("该试验已经收获，无需再授粉");
        }
        //先判断是否有进行中的授粉，如果有则不能下载excel
        List<TcPollinationSingleNumTb> pollinationSingleNumTbList = tcPollinationSingleNumTbMapper.selectAllByExperimentNumOrderByIdDesc(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum());
        if (CollectionUtil.isNotEmpty(pollinationSingleNumTbList)) {
            TcPollinationSingleNumTb first = pollinationSingleNumTbList.get(0);
            if (StringUtils.isNotEmpty(first.getPollinationApplyNum())) {
                BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(first.getPollinationApplyNum());
                if(BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())){
                    throw new BusinessException("该试验已经有一个授粉正在审批中，请等待执行完毕后再生成授粉excel");
                }
            }

        }
        //先下载授粉表格，校验授粉模板是否存在
        String excelTemplateName = "田测授粉数据表单模板V1.0.xlsx";
        String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
        try {
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }

        //判断上次下载的授粉表格是否没有进行授粉，如果是则清空上次表格数据，同时判断授粉编号是否连续，如果是代表两次下载授粉表格之间，没有发起取样检测操作，可以重置到上次生成授粉单株编号的起始位置
        List<TcPollinationSingleNumTb> noPollinationtcPollinationSingleNumTbList = tcPollinationSingleNumTbMapper.selectAllByExperimentNumAndPollinationApplyNumIsNull(tcExperimentTb.getExperimentNum());
        if (CollectionUtil.isNotEmpty(noPollinationtcPollinationSingleNumTbList)) {
            tcPollinationSingleNumTbMapper.deleteByExperimentNumAndPollinationApplyNumIsNull(tcExperimentTb.getExperimentNum());
            Integer beginSingleNumber = Integer.valueOf(noPollinationtcPollinationSingleNumTbList.get(0).getSingleNumber().substring(3));
            Integer endSingleNumber = Integer.valueOf(noPollinationtcPollinationSingleNumTbList.get(noPollinationtcPollinationSingleNumTbList.size() - 1).getSingleNumber().substring(3));
            if (endSingleNumber.equals(tcExperimentTb.getNextSampleNumber() - 1)) {
                tcExperimentTb.setNextSampleNumber(beginSingleNumber);
                tcExperimentTbMapper.updateById(tcExperimentTb);
            }
        }

        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(tcExperimentTb.getSpeciesCode());
        Map<String, String> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        //循环遍历excel
        for (TcPollinationCreatePollinationExcelReqDTO.Content content : tcPollinationCreatePollinationExcelReqDTO.getContentList()) {
            TcExperimentDesignTb tcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum(), content.getRegionNum(), content.getSeedNum());
            if (tcExperimentDesignTb == null) {
                throw new BusinessException("数据异常，找不到此试验设计种子信息 试验：" + tcPollinationCreatePollinationExcelReqDTO.getExperimentNum() + "种子号：" + content.getSeedNum() + "区域：" + content.getRegionNum());
            }

            //没有单株编号,非单株取样
            if (BioDrQiContents.N.equals(content.getSinglePlantFlag())) {
                if (PollinationParentFlagEnum.father.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, null, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                    fatherList.add(tcPollinationExcelDTO);

                } else if (PollinationParentFlagEnum.mother.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, null, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                    matherList.add(tcPollinationExcelDTO);
                } else if (PollinationParentFlagEnum.parent.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO fatherTcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, null, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                    fatherList.add(fatherTcPollinationExcelDTO);
                    TcPollinationExcelDTO matherTcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, null, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                    matherList.add(matherTcPollinationExcelDTO);
                }
            } else {
                List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNumAndSeedNumAndRegionNumAndCheckResult(tcPollinationCreatePollinationExcelReqDTO.getSampleApplyNum(), content.getSeedNum(), content.getRegionNum(), SampleTestCheckResultEnum.stay.name());
                List<String> sampleCodeList = tcSampleTestTbList.stream().map(TcSampleTestTb::getSampleCode).distinct().collect(Collectors.toList());
                for (int i = 0; i < content.getSinglePlantNumber(); i++) {
                    String sampleCode = null;
                    if (i < sampleCodeList.size()) {
                        sampleCode = sampleCodeList.get(i);
                    } else {
                        //生成单株编号，同时更新取样编号的下次开始编号
                        sampleCode = tcExperimentTb.getSampleCodePrefix() + tcExperimentTb.getNextSampleNumber();
                        tcExperimentTb.setNextSampleNumber(tcExperimentTb.getNextSampleNumber() + 1);

                        TcPollinationSingleNumTb tcPollinationSingleNumTb = new TcPollinationSingleNumTb();
                        tcPollinationSingleNumTb.setExperimentNum(tcExperimentTb.getExperimentNum());
                        tcPollinationSingleNumTb.setPollinationApplyNum(null);
                        tcPollinationSingleNumTb.setSeedNum(content.getSeedNum());
                        tcPollinationSingleNumTb.setRegionNum(content.getRegionNum());
                        tcPollinationSingleNumTb.setSingleNumber(sampleCode);
                        tcPollinationSingleNumTb.setCreateTime(new Date());
                        tcPollinationSingleNumTb.setCreateUserName(SecurityContextHolder.getNickName());
                        tcPollinationSingleNumTb.setTcSingleNumber(tcPollinationSingleNumTb.getRegionNum() + tcPollinationSingleNumTb.getSingleNumber().substring(3));
                        currentTcPollinationSingleNumTbList.add(tcPollinationSingleNumTb);
                    }
                    if (PollinationParentFlagEnum.father.name().equals(content.getParentFlag())) {

                        TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, sampleCode, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                        fatherList.add(tcPollinationExcelDTO);
                    } else if (PollinationParentFlagEnum.mother.name().equals(content.getParentFlag())) {
                        TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, sampleCode, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                        matherList.add(tcPollinationExcelDTO);
                    } else if (PollinationParentFlagEnum.parent.name().equals(content.getParentFlag())) {
                        TcPollinationExcelDTO fatherTcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, sampleCode, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                        fatherList.add(fatherTcPollinationExcelDTO);
                        TcPollinationExcelDTO matherTcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, sampleCode, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                        matherList.add(matherTcPollinationExcelDTO);
                    }
                }
            }
        }
        if (CollectionUtil.isNotEmpty(fatherList)) {
            for (int i = 0; i < fatherList.size(); i++) {
                if (i < matherList.size()) {
                    TcPollinationExcelDTO matherTcPollinationExcelDTO = matherList.get(i);
                    TcPollinationExcelDTO fatherTcPollinationExcelDTO = fatherList.get(i);
                    matherTcPollinationExcelDTO.setFatherRegionNum(fatherTcPollinationExcelDTO.getFatherRegionNum());
                    matherTcPollinationExcelDTO.setFatherSeedNum(fatherTcPollinationExcelDTO.getFatherSeedNum());
                    matherTcPollinationExcelDTO.setFatherSampleCode(null);
                    matherTcPollinationExcelDTO.setFatherBreedName(fatherTcPollinationExcelDTO.getFatherBreedName());
                    matherTcPollinationExcelDTO.setFatherVectorTaskCode(fatherTcPollinationExcelDTO.getFatherVectorTaskCode());
                    matherTcPollinationExcelDTO.setFatherGenerationName(fatherTcPollinationExcelDTO.getFatherGenerationName());
                    matherTcPollinationExcelDTO.setFatherTcGene(fatherTcPollinationExcelDTO.getFatherTcGene());
                    matherTcPollinationExcelDTO.setFatherSampleCode(fatherTcPollinationExcelDTO.getFatherSampleCode());
                } else {
                    matherList.add(fatherList.get(i));
                }
            }
        }
        //判断是否有单株编号生成
        if (CollectionUtil.isNotEmpty(currentTcPollinationSingleNumTbList)) {
            tcPollinationSingleNumTbMapper.insertBatch(currentTcPollinationSingleNumTbList);
            tcExperimentTbMapper.updateById(tcExperimentTb);
        }
        return matherList;
        //  ExcelUtil.fillExcel(templateDir, matherList, TcPollinationExcelDTO.class, httpServletResponse);
    }


    @Override
    public void exportPollinationExcel(TcPollinationExportPollinationExcelReqDTO tcPollinationExportPollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcPollinationExportPollinationExcelReqDTO.getExperimentNum());
        if (tcExperimentTb == null) {
            throw new BusinessException("试验方案不存在");
        }

        List<TcPollinationExcelDTO> tcPollinationExcelDTOList = new ArrayList<>();
        for (TcPollinationExportPollinationExcelReqDTO.Content content : tcPollinationExportPollinationExcelReqDTO.getContentList()) {
            TcPollinationExcelDTO tcPollinationExcelDTO = new TcPollinationExcelDTO();
            tcPollinationExcelDTO.setMotherRegionNum(content.getMotherRegionNum());
            tcPollinationExcelDTO.setMotherSeedNum(content.getMotherSeedNum());
            tcPollinationExcelDTO.setMotherSampleCode(content.getMotherSampleCode());
            tcPollinationExcelDTO.setMotherBreedName(content.getMotherBreedName());
            tcPollinationExcelDTO.setMotherVectorTaskCode(content.getMotherVectorTaskCode());
            tcPollinationExcelDTO.setMotherGenerationName(content.getMotherGenerationName());
            tcPollinationExcelDTO.setMotherTcGene(content.getMotherTcGene());
            tcPollinationExcelDTO.setFatherRegionNum(content.getFatherRegionNum());
            tcPollinationExcelDTO.setFatherSeedNum(content.getFatherSeedNum());
            tcPollinationExcelDTO.setFatherSampleCode(content.getFatherSampleCode());
            tcPollinationExcelDTO.setFatherBreedName(content.getFatherBreedName());
            tcPollinationExcelDTO.setFatherVectorTaskCode(content.getFatherVectorTaskCode());
            tcPollinationExcelDTO.setFatherGenerationName(content.getFatherGenerationName());
            tcPollinationExcelDTO.setFatherTcGene(content.getFatherTcGene());
            tcPollinationExcelDTO.setPollinationDate(null);
            tcPollinationExcelDTO.setHarvestTypeName(null);
            tcPollinationExcelDTO.setHarvestTypeCode(null);
            tcPollinationExcelDTO.setRemark(null);
            tcPollinationExcelDTOList.add(tcPollinationExcelDTO);
        }

        //先下载授粉表格，校验授粉模板是否存在
        String excelTemplateName = "田测授粉结果表单模板V1.0.xlsx";
        String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
        try {
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
        } catch (Exception e) {
            log.error("模板下载失败，", e);
            throw new BusinessException("模板下载失败，请联系管理员检测模板配置");
        }
        ExcelUtil.fillExcel(templateDir, tcPollinationExcelDTOList, TcPollinationExcelDTO.class, httpServletResponse);
    }
}
