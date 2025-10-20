package com.bio.drqi.tc.service.impl;

import java.util.*;

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
        //进行中授粉校验，如果有进行中授粉，不能再次发起新的授粉，因为新发起的授粉会清空旧的授粉虚拟单株编号
        List<String> regionNumList = tcPollinationCreatePollinationExcelReqDTO.getContentList().stream().map(TcPollinationCreatePollinationExcelReqDTO.Content::getRegionNum).collect(Collectors.toList());
        for (String regionNum : regionNumList) {
            //先判断是否有进行中的授粉，如果有则不能下载excel
            List<TcPollinationSingleNumTb> pollinationSingleNumTbList = tcPollinationSingleNumTbMapper.selectAllByExperimentNumAndRegionNumOrderByIdDesc(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum(), regionNum);
            if (CollectionUtil.isNotEmpty(pollinationSingleNumTbList)) {
                TcPollinationSingleNumTb first = pollinationSingleNumTbList.get(0);
                if (StringUtils.isNotEmpty(first.getPollinationApplyNum())) {
                    BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(first.getPollinationApplyNum());
                    if (BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
                        throw new BusinessException("如果有未取样苗参与授粉，为保证序号连续，一个试验的一个小区在一个时间段内只能发起一个授粉，请先执行完毕上次授粉工单");
                    }
                }

            }
        }

        //清空上次下载授粉表格数据
        tcPollinationSingleNumTbMapper.deleteByExperimentNumAndPollinationApplyNumIsNull(tcExperimentTb.getExperimentNum());

        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(tcExperimentTb.getSpeciesCode());
        Map<String, String> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        //找到这个试验的所有的取样编号,并按照小区分类
        Map<String, List<TcSampleTestTb>> reginTcSampleTestTbListMap = tcSampleTestTbMapper.selectAllByExperimentNum(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum()).stream().collect(Collectors.groupingBy(TcSampleTestTb::getRegionNum));
        //循环选中的授粉数据
        for (TcPollinationCreatePollinationExcelReqDTO.Content content : tcPollinationCreatePollinationExcelReqDTO.getContentList()) {
            //一定要清空
            currentTcPollinationSingleNumTbList.clear();
            TcExperimentDesignTb tcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum(), content.getRegionNum(), content.getSeedNum());
            if (tcExperimentDesignTb == null) {
                throw new BusinessException("数据异常，找不到此试验设计种子信息 试验：" + tcPollinationCreatePollinationExcelReqDTO.getExperimentNum() + "种子号：" + content.getSeedNum() + "区域：" + content.getRegionNum());
            }
            //没有单株编号,非单株取样
            if (BioDrQiContents.N.equals(content.getSinglePlantFlag())) {
                if (PollinationParentFlagEnum.father.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, null, null, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                    fatherList.add(tcPollinationExcelDTO);

                } else if (PollinationParentFlagEnum.mother.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, null, null, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                    matherList.add(tcPollinationExcelDTO);
                } else if (PollinationParentFlagEnum.parent.name().equals(content.getParentFlag())) {
                    TcPollinationExcelDTO fatherTcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, null, null, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                    fatherList.add(fatherTcPollinationExcelDTO);
                    TcPollinationExcelDTO matherTcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, null, null, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                    matherList.add(matherTcPollinationExcelDTO);
                }

            } else {
                //单株授粉，如果取样编号不够，需要生成虚拟的单株编号
                List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNumAndRegionNumAndSeedNum(tcPollinationCreatePollinationExcelReqDTO.getSampleApplyNum(), content.getRegionNum(), content.getSeedNum());
                Map<String, List<TcSampleTestTb>> tcSampleCodeListMap = tcSampleTestTbList.stream().collect(Collectors.groupingBy(TcSampleTestTb::getTcSampleCode));
                List<String> tcSampleCodeList = tcSampleTestTbList.stream().map(TcSampleTestTb::getTcSampleCode).distinct().collect(Collectors.toList());
                List<TcPollinationSingleNumTb> tcPollinationSingleNumTbList = tcPollinationSingleNumTbMapper.selectAllByExperimentNumAndRegionNumOrderByIdDesc(tcPollinationCreatePollinationExcelReqDTO.getExperimentNum(), content.getRegionNum());
                //找出当前最大的取样编号
                List<String> tcSingleNumberList = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(tcPollinationSingleNumTbList)) {
                    tcSingleNumberList.addAll(tcPollinationSingleNumTbList.stream().map(TcPollinationSingleNumTb::getTcSingleNumber).collect(Collectors.toList()));
                }
                if (reginTcSampleTestTbListMap.get(content.getRegionNum()) != null) {
                    tcSingleNumberList.addAll(reginTcSampleTestTbListMap.get(content.getRegionNum()).stream().map(TcSampleTestTb::getTcSampleCode).collect(Collectors.toList()));
                }
                Integer maxNumber = null;
                if (CollectionUtil.isNotEmpty(tcSampleCodeList)) {
                    maxNumber = tcSingleNumberList.stream().distinct().map(tcSingleNumber -> Integer.valueOf(tcSingleNumber.substring(content.getRegionNum().length()))).max(Integer::compare).get();
                }

                for (int i = 0; i < content.getSinglePlantNumber(); i++) {
                    String tcSampleCode = null;
                    if (i < tcSampleCodeList.size()) {
                        tcSampleCode = tcSampleCodeList.get(i);
                    } else {
                        maxNumber = maxNumber == null ? 1 : maxNumber + 1;
                        tcSampleCode = content.getRegionNum() + StringUtils.padl(maxNumber.toString(), 3, '0');

                        TcPollinationSingleNumTb tcPollinationSingleNumTb = new TcPollinationSingleNumTb();
                        tcPollinationSingleNumTb.setExperimentNum(tcExperimentTb.getExperimentNum());
                        tcPollinationSingleNumTb.setPollinationApplyNum(null);
                        tcPollinationSingleNumTb.setSeedNum(content.getSeedNum());
                        tcPollinationSingleNumTb.setRegionNum(content.getRegionNum());
                        tcPollinationSingleNumTb.setCreateTime(new Date());
                        tcPollinationSingleNumTb.setCreateUserName(SecurityContextHolder.getNickName());
                        tcPollinationSingleNumTb.setTcSingleNumber(tcSampleCode);
                        currentTcPollinationSingleNumTbList.add(tcPollinationSingleNumTb);
                    }
                    if (PollinationParentFlagEnum.father.name().equals(content.getParentFlag())) {
                        TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, CollectionUtil.isNotEmpty(tcSampleCodeListMap.get(tcSampleCode)) ? tcSampleCodeListMap.get(tcSampleCode).get(0).getSampleCode() : null, tcSampleCode, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                        fatherList.add(tcPollinationExcelDTO);
                    } else if (PollinationParentFlagEnum.mother.name().equals(content.getParentFlag())) {
                        TcPollinationExcelDTO tcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, CollectionUtil.isNotEmpty(tcSampleCodeListMap.get(tcSampleCode)) ? tcSampleCodeListMap.get(tcSampleCode).get(0).getSampleCode() : null, tcSampleCode, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                        matherList.add(tcPollinationExcelDTO);
                    } else if (PollinationParentFlagEnum.parent.name().equals(content.getParentFlag())) {
                        TcPollinationExcelDTO fatherTcPollinationExcelDTO = TcPollinationExcelDTO.ofFather(tcExperimentDesignTb, CollectionUtil.isNotEmpty(tcSampleCodeListMap.get(tcSampleCode)) ? tcSampleCodeListMap.get(tcSampleCode).get(0).getSampleCode() : null, tcSampleCode, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                        fatherList.add(fatherTcPollinationExcelDTO);
                        TcPollinationExcelDTO matherTcPollinationExcelDTO = TcPollinationExcelDTO.ofMather(tcExperimentDesignTb, CollectionUtil.isNotEmpty(tcSampleCodeListMap.get(tcSampleCode)) ? tcSampleCodeListMap.get(tcSampleCode).get(0).getSampleCode() : null, tcSampleCode, codeNameCerBreedDictMap.get(tcExperimentDesignTb.getBreedCode()));
                        matherList.add(matherTcPollinationExcelDTO);
                    }
                }
            }

            //判断是否有单株编号生成
            if (CollectionUtil.isNotEmpty(currentTcPollinationSingleNumTbList)) {
                tcPollinationSingleNumTbMapper.insertBatch(currentTcPollinationSingleNumTbList);
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
                    matherTcPollinationExcelDTO.setFatherTcSampleCode(fatherTcPollinationExcelDTO.getFatherTcSampleCode());
                } else {
                    matherList.add(fatherList.get(i));
                }
            }
        }
        return matherList;
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
            tcPollinationExcelDTO.setMotherTcSampleCode(content.getMotherTcSampleCode());
            tcPollinationExcelDTO.setMotherBreedName(content.getMotherBreedName());
            tcPollinationExcelDTO.setMotherVectorTaskCode(content.getMotherVectorTaskCode());
            tcPollinationExcelDTO.setMotherGenerationName(content.getMotherGenerationName());
            tcPollinationExcelDTO.setMotherTcGene(content.getMotherTcGene());
            tcPollinationExcelDTO.setFatherRegionNum(content.getFatherRegionNum());
            tcPollinationExcelDTO.setFatherSeedNum(content.getFatherSeedNum());
            tcPollinationExcelDTO.setFatherTcSampleCode(content.getFatherTcSampleCode());
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
