package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ConversionAndTransTypeEnum;
import com.bio.drqi.common.enums.PlantStatusEnum;
import com.bio.drqi.enums.VectorTaskStatusEnum;
import com.bio.drqi.manage.dto.project.ConversionAndTransDTO;
import com.bio.drqi.manage.service.project.CerConversionAndTransService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.manage.project.req.CerConversionAndTransConfirmReqDTO;
import com.bio.drqi.manage.project.req.ConversionAndTransDetailReqDTO;
import com.bio.drqi.manage.project.req.ConversionAndTransReqDTO;
import com.bio.drqi.manage.project.rsp.ConversionAndTransDetailRspDTO;
import com.bio.drqi.manage.project.rsp.ConversionAndTransRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CerConversionAndTransServiceImpl implements CerConversionAndTransService {

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;


    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Override
    public PageInfo<ConversionAndTransRspDTO> listPage(ConversionAndTransReqDTO conversionAndTransReqDTO) {
        PageHelper.startPage(conversionAndTransReqDTO.getPageNum(), conversionAndTransReqDTO.getPageSize());
        List<CerConversionAndTransTb> cerConversionAndTransTbList = cerConversionAndTransTbMapper.selectSelective(BeanUtils.copyProperties(conversionAndTransReqDTO, CerConversionAndTransTb.class));
        PageInfo<CerConversionAndTransTb> srcPageInfo = new PageInfo<>(cerConversionAndTransTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, ConversionAndTransRspDTO.class);
    }

    @Override
    public List<ConversionAndTransRspDTO> listByVectorTask(Integer vectorTaskId) {
        List<ConversionAndTransRspDTO> conversionAndTransByVectorTaskRspDTOList = new ArrayList<>();
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTaskId);
        List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectAllByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        if (CollectionUtil.isNotEmpty(cerConversionAndTransRefList)) {
            List<CerConversionAndTransTb> cerConversionAndTransTbList = cerConversionAndTransTbMapper.selectBatchIds(cerConversionAndTransRefList.stream().map(CerConversionAndTransRef::getConversionAndTransId).distinct().collect(Collectors.toList()));
            return BeanUtils.copyListProperties(cerConversionAndTransTbList, ConversionAndTransRspDTO.class);
        }
        return conversionAndTransByVectorTaskRspDTOList;
    }

    @Override
    public PageInfo<ConversionAndTransDetailRspDTO> listPageDetail(ConversionAndTransDetailReqDTO conversionAndTransDetailReqDTO) {
        PageHelper.startPage(conversionAndTransDetailReqDTO.getPageNum(), conversionAndTransDetailReqDTO.getPageSize());
        List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectSelective(BeanUtils.copyProperties(conversionAndTransDetailReqDTO, CerConversionAndTransRef.class));
        PageInfo<CerConversionAndTransRef> srcPageInfo = new PageInfo<>(cerConversionAndTransRefList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, ConversionAndTransDetailRspDTO.class);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transAccept(CerConversionAndTransConfirmReqDTO cerConversionAndTransConfirmReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerConversionAndTransConfirmReqDTO.getTaskNum());
        if (bioTaskDtlTb == null) {
            throw new BusinessException("不存在此工单:" + cerConversionAndTransConfirmReqDTO.getTaskNum());
        }
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("不是执行中工单");
        }
        ConversionAndTransDTO conversionAndTransDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ConversionAndTransDTO.class);

        //初始化移苗记录
        CerConversionAndTransTb cerConversionAndTransTb = initcerConversionAndTransTb(bioTaskDtlTb, conversionAndTransDTO);


        for (CerConversionAndTransConfirmReqDTO.Content content : cerConversionAndTransConfirmReqDTO.getContentList()) {
            //转化移苗
            if (StringUtils.isNotEmpty(content.getTransformCode())) {
                List<ConversionAndTransDTO.TransForm> transFormList = conversionAndTransDTO.getTransFormList().stream().filter(transForm -> StringUtils.equals(transForm.getTransformCode(), content.getTransformCode()) && StringUtils.equals(transForm.getVectorTaskCode(), content.getVectorTaskCode())).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(transFormList)) {
                    throw new BusinessException("移苗确认参数异常，联系相管人员");
                }
                transFormList.get(0).setDealResult(content.getDealResult());
                if (!CerProjectContents.Y.equals(content.getDealResult())) {
                    continue;
                }
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(content.getVectorTaskCode());
                if (cerVectorTaskTb == null) {
                    throw new BusinessException("实施方案找不到 ");
                }
                if (!VectorTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
                    throw new BusinessException("实施方案不是执行中状态,当前实施方案:" + cerVectorTaskTb.getVectorTaskCode());
                }
                transFormList.get(0).setAcceptNum(content.getAcceptNum());
                CerConversionAndTransRef cerConversionAndTransRef = new CerConversionAndTransRef();
                cerConversionAndTransRef.setConversionAndTransId(cerConversionAndTransTb.getId());
                cerConversionAndTransRef.setAcceptorMaterial(transFormList.get(0).getAcceptorMaterial());
                cerConversionAndTransRef.setVectorTaskCode(transFormList.get(0).getVectorTaskCode());
                cerConversionAndTransRef.setTransformCode(transFormList.get(0).getTransformCode());
                cerConversionAndTransRef.setTransNum(transFormList.get(0).getAcceptNum());
                cerConversionAndTransRef.setTransGeneFlag(transFormList.get(0).getTransGeneFlag());
                cerConversionAndTransRef.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
                cerConversionAndTransRef.setProjectCode(cerVectorTaskTb.getProjectCode());
                cerConversionAndTransRef.setPlasmidName(transFormList.get(0).getPlasmidName());
                cerConversionAndTransRef.setRemark(transFormList.get(0).getRemark());
                cerConversionAndTransRef.setTaskNum(bioTaskDtlTb.getTaskNum());
                cerConversionAndTransRef.setCreateTime(new Date());
                cerConversionAndTransRef.setCreateUserId(cerConversionAndTransTb.getCreateUserId());
                cerConversionAndTransRef.setCreateUserName(cerConversionAndTransTb.getCreateUserName());
                cerConversionAndTransRefMapper.insert(cerConversionAndTransRef);

                //更新总移苗数量
                cerConversionAndTransTb.setTransNumber(transFormList.get(0).getAcceptNum() + (cerConversionAndTransTb.getTransNumber() == null ? 0 : cerConversionAndTransTb.getTransNumber()));
                cerConversionAndTransTbMapper.updateById(cerConversionAndTransTb);

                //同步移苗数据到CER临时库
                PlantMultipleStockTb plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByVectorTaskCodeAndTransformCode(transFormList.get(0).getVectorTaskCode(), transFormList.get(0).getTransformCode());
                if (plantMultipleStockTb == null) {
                    plantMultipleStockTb = new PlantMultipleStockTb();
                    plantMultipleStockTb.setSeedNum(null);
                    plantMultipleStockTb.setTransformCode(transFormList.get(0).getTransformCode());
                    plantMultipleStockTb.setGeneration(GenerationEnum.T0.code);
                    plantMultipleStockTb.setPlantNumber(transFormList.get(0).getTransNum());
                    plantMultipleStockTb.setSourceCode(SourceCodeEnum.project.name());
                    plantMultipleStockTb.setRemark("转化移苗数据");
                    plantMultipleStockTb.setCreateTime(new Date());
                    plantMultipleStockTb.setCreateUserId(SecurityContextHolder.getUserId());
                    plantMultipleStockTb.setCreateUserName(SecurityContextHolder.getNickName());
                    plantMultipleStockTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                    plantMultipleStockTb.setSpeciesCode(cerVectorTaskTb.getSpeciesCode());
                    plantMultipleStockTb.setBreedCode(cerVectorTaskTb.getBreedCode());
                    plantMultipleStockTb.setSampleNumber(0);
                    plantMultipleStockTb.setCurrentNumber(transFormList.get(0).getTransNum());
                    plantMultipleStockTb.setRegionNum(null);
                    plantMultipleStockTb.setVectorTaskCode(transFormList.get(0).getVectorTaskCode());
                    plantMultipleStockTb.setPdImplementCode(null);
                    plantMultipleStockTb.setPlantDate(DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN));
                    plantMultipleStockTbMapper.insert(plantMultipleStockTb);
                } else {
                    plantMultipleStockTb.setPlantNumber(transFormList.get(0).getAcceptNum() + (plantMultipleStockTb.getPlantNumber()==null?0:plantMultipleStockTb.getPlantNumber()));
                    plantMultipleStockTb.setCurrentNumber(transFormList.get(0).getAcceptNum() + (plantMultipleStockTb.getCurrentNumber()==null?0:plantMultipleStockTb.getCurrentNumber()));
                    plantMultipleStockTbMapper.updateById(plantMultipleStockTb);
                }


                //取样疫苗
            } else if (StringUtils.isNotEmpty(content.getSampleCode())) {
                List<ConversionAndTransDTO.SampleCode> sampleCodeList = conversionAndTransDTO.getSampleCodeList().stream().filter(sample -> StringUtils.equals(sample.getSampleCode(), content.getSampleCode()) && StringUtils.equals(sample.getVectorTaskCode(), content.getVectorTaskCode())).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(sampleCodeList)) {
                    throw new BusinessException("移苗确认参数异常，联系相管人员");
                }
                sampleCodeList.get(0).setDealResult(content.getDealResult());
                if (!CerProjectContents.Y.equals(content.getDealResult())) {
                    continue;
                }
                BioSampleTestTb bioSampleTestTb = bioSampleTestTbMapper.selectOneByVectorTaskCodeAndSampleCodeFirst(content.getVectorTaskCode(), content.getSampleCode());
                if (bioSampleTestTb == null) {
                    throw new BusinessException("取样编号不存在" + content.getSampleCode());
                }
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
                if (cerVectorTaskTb == null) {
                    throw new BusinessException("数据异常，取样数据中无实施方案编号，异常取样编号：" + bioSampleTestTb.getSampleCode());
                }
                CerConversionAndTransRef cerConversionAndTransRef = new CerConversionAndTransRef();
                cerConversionAndTransRef.setConversionAndTransId(cerConversionAndTransTb.getId());
                cerConversionAndTransRef.setSampleCode(sampleCodeList.get(0).getSampleCode());
                cerConversionAndTransRef.setEditPureUnion(sampleCodeList.get(0).getEditPureUnion());
                cerConversionAndTransRef.setAcceptorMaterial(sampleCodeList.get(0).getAcceptorMaterial());
                cerConversionAndTransRef.setVectorTaskCode(sampleCodeList.get(0).getVectorTaskCode());
                cerConversionAndTransRef.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
                cerConversionAndTransRef.setProjectCode(cerVectorTaskTb.getProjectCode());
                cerConversionAndTransRef.setTransGeneFlag(sampleCodeList.get(0).getTransGeneFlag());
                cerConversionAndTransRef.setPlasmidName(sampleCodeList.get(0).getPlasmidName());
                cerConversionAndTransRef.setAcceptorMaterial(sampleCodeList.get(0).getAcceptorMaterial());
                cerConversionAndTransRef.setTaskNum(bioTaskDtlTb.getTaskNum());
                cerConversionAndTransRef.setRemark(sampleCodeList.get(0).getRemark());
                cerConversionAndTransRef.setCreateTime(cerConversionAndTransTb.getCreateTime());
                cerConversionAndTransRef.setCreateUserId(cerConversionAndTransTb.getCreateUserId());
                cerConversionAndTransRef.setCreateUserName(cerConversionAndTransTb.getCreateUserName());
                cerConversionAndTransRefMapper.insert(cerConversionAndTransRef);

                //更新总移苗数量
                cerConversionAndTransTb.setTransNumber((cerConversionAndTransTb.getTransNumber() == null ? 0 : cerConversionAndTransTb.getTransNumber()) + 1);
                cerConversionAndTransTbMapper.updateById(cerConversionAndTransTb);

                PlantSingleStockTb plantSingleStockTb = PlantSingleStockTb.of(bioSampleTestTb, PlantStatusEnum.STATUS_1, DateUtil.format(new Date(),DatePattern.NORM_DATE_PATTERN), bioTaskDtlTb.getTaskNum(),SourceCodeEnum.project.name(), "移苗取样数据");
                if (Objects.isNull(plantSingleStockTbMapper.selectOneByPlantCode(plantSingleStockTb.getPlantCode()))) {
                    plantSingleStockTbMapper.insert(plantSingleStockTb);
                }

            }

        }

        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(conversionAndTransDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
    }

    @NotNull
    private CerConversionAndTransTb initcerConversionAndTransTb(BioTaskDtlTb bioTaskDtlTb, ConversionAndTransDTO conversionAndTransDTO) {
        CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
        if (cerConversionAndTransTb == null) {
            cerConversionAndTransTb = new CerConversionAndTransTb();
            cerConversionAndTransTb.setHandoverDate(conversionAndTransDTO.getHandoverDate());
            cerConversionAndTransTb.setCreateTime(new Date());
            cerConversionAndTransTb.setCreateUserId(SecurityContextHolder.getUserId());
            cerConversionAndTransTb.setCreateUserName(SecurityContextHolder.getNickName());
            cerConversionAndTransTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            cerConversionAndTransTb.setRemark(conversionAndTransDTO.getRemark());
            cerConversionAndTransTb.setTransNumber(0);
            cerConversionAndTransTb.setTransType(CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList()) ? ConversionAndTransTypeEnum.trans.name() : ConversionAndTransTypeEnum.sample.name());
            cerConversionAndTransTb.setImageUrl(JSONUtil.toJsonStr(conversionAndTransDTO.getImageUrlList()));
            cerConversionAndTransTbMapper.insert(cerConversionAndTransTb);
        }
        return cerConversionAndTransTb;
    }
}
