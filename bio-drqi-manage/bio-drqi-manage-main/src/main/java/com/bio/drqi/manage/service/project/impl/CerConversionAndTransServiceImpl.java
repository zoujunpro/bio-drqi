package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ConversionAndTransTypeEnum;
import com.bio.drqi.manage.dto.project.ConversionAndTransDTO;
import com.bio.drqi.manage.service.project.CerConversionAndTransService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.project.req.CerConversionAndTransConfirmReqDTO;
import com.bio.drqi.project.req.ConversionAndTransDetailReqDTO;
import com.bio.drqi.project.req.ConversionAndTransReqDTO;
import com.bio.drqi.project.rsp.ConversionAndTransDetailRspDTO;
import com.bio.drqi.project.rsp.ConversionAndTransRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Override
    public PageInfo<ConversionAndTransRspDTO> listPage(ConversionAndTransReqDTO conversionAndTransReqDTO) {
        PageHelper.startPage(conversionAndTransReqDTO.getPageNum(), conversionAndTransReqDTO.getPageSize());
        List<CerConversionAndTransTb> cerConversionAndTransTbList = cerConversionAndTransTbMapper.selectAllOrderByIdDesc();
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
        CerConversionAndTransRef cerConversionAndTransRef = new CerConversionAndTransRef();
        cerConversionAndTransRef.setConversionAndTransId(conversionAndTransDetailReqDTO.getId());
        if(conversionAndTransDetailReqDTO.getVectorTaskId()!=null){
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(conversionAndTransDetailReqDTO.getVectorTaskId());
            cerConversionAndTransRef.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        }
        PageHelper.startPage(conversionAndTransDetailReqDTO.getPageNum(), conversionAndTransDetailReqDTO.getPageSize());
        List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectSelective(cerConversionAndTransRef);
        PageInfo<CerConversionAndTransRef> srcPageInfo = new PageInfo<>(cerConversionAndTransRefList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo,ConversionAndTransDetailRspDTO.class);
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

        CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
        if (cerConversionAndTransTb == null) {
            cerConversionAndTransTb = new CerConversionAndTransTb();
            cerConversionAndTransTb.setHandoverDate(conversionAndTransDTO.getHandoverDate());
            cerConversionAndTransTb.setCreateTime(new Date());
            cerConversionAndTransTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
            cerConversionAndTransTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
            cerConversionAndTransTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            cerConversionAndTransTb.setRemark(conversionAndTransDTO.getRemark());
            cerConversionAndTransTb.setTransNumber(0);
            cerConversionAndTransTb.setTransType(CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList()) ? ConversionAndTransTypeEnum.trans.name() : ConversionAndTransTypeEnum.sample.name());
            cerConversionAndTransTb.setImageUrl(JSONUtil.toJsonStr(conversionAndTransDTO.getImageUrlList()));
            cerConversionAndTransTbMapper.insert(cerConversionAndTransTb);
        }

        for (CerConversionAndTransConfirmReqDTO.Content content : cerConversionAndTransConfirmReqDTO.getContentList()) {
            if (StringUtils.isNotEmpty(content.getTransformCode())) {
                List<ConversionAndTransDTO.TransForm> transFormList = conversionAndTransDTO.getTransFormList().stream().filter(transForm -> StringUtils.equals(transForm.getTransformCode(), content.getTransformCode()) && StringUtils.equals(transForm.getVectorTaskCode(), content.getVectorTaskCode())).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(transFormList)) {
                    throw new BusinessException("移苗确认参数异常，联系相管人员");
                }
                transFormList.get(0).setDealResult(content.getDealResult());
                if (!CerProjectContents.Y.equals(content.getDealResult())) {
                    continue;
                }
                transFormList.get(0).setAcceptNum(content.getAcceptNum());
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(content.getVectorTaskCode());
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
                cerConversionAndTransRefMapper.insert(cerConversionAndTransRef);

                //更新总移苗数量
                cerConversionAndTransTb.setTransNumber(transFormList.get(0).getAcceptNum()+(cerConversionAndTransTb.getTransNumber()==null?0:cerConversionAndTransTb.getTransNumber()));
                cerConversionAndTransTbMapper.updateById(cerConversionAndTransTb);
            } else if (StringUtils.isNotEmpty(content.getSampleCode())) {
                List<ConversionAndTransDTO.SampleCode> sampleCodeList = conversionAndTransDTO.getSampleCodeList().stream().filter(sample -> StringUtils.equals(sample.getSampleCode(), content.getSampleCode()) && StringUtils.equals(sample.getVectorTaskCode(), content.getVectorTaskCode())).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(sampleCodeList)) {
                    throw new BusinessException("移苗确认参数异常，联系相管人员");
                }
                sampleCodeList.get(0).setDealResult(content.getDealResult());
                if (!CerProjectContents.Y.equals(content.getDealResult())) {
                    continue;
                }
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(content.getVectorTaskCode());
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
                cerConversionAndTransRefMapper.insert(cerConversionAndTransRef);
                //更新总移苗数量
                cerConversionAndTransTb.setTransNumber((cerConversionAndTransTb.getTransNumber()==null?0:cerConversionAndTransTb.getTransNumber())+1);
                cerConversionAndTransTbMapper.updateById(cerConversionAndTransTb);

            }

        }

        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(conversionAndTransDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
    }
}
