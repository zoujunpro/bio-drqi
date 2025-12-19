package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.manage.transform.req.ApprovePassTransformQueryReqDTO;
import com.bio.drqi.manage.transform.req.TransformListByVectorTaskReqDTO;
import com.bio.drqi.manage.transform.req.TransformListByVectorTaskRspDTO;
import com.bio.drqi.manage.transform.req.TransformListPageReqDTO;
import com.bio.drqi.manage.transform.rsp.ApprovePassTransformQueryRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerTransformTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.service.project.TransformService;
import com.bio.drqi.manage.transform.rsp.TransformListPageRspDTO;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.CerTransformTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransformServiceImpl implements TransformService {


    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;


    @Override
    public PageInfo<TransformListPageRspDTO> listPage(TransformListPageReqDTO transformListPageReqDTO) {
        PageHelper.startPage(transformListPageReqDTO.getPageNum(), transformListPageReqDTO.getPageSize());

        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectSelective(BeanUtils.copyProperties(transformListPageReqDTO, CerTransformTb.class));
        PageInfo<CerTransformTb> srcPageInfo = new PageInfo<>(cerTransformTbList);
        PageInfo<TransformListPageRspDTO> rspDTOPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TransformListPageRspDTO.class);
        Map<String, String> cerSpeciesMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        Map<String, String> cerBreedMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        if (CollectionUtil.isNotEmpty(rspDTOPageInfo.getList())) {
            rspDTOPageInfo.getList().forEach(transformListPageRspDTO -> {
                transformListPageRspDTO.setSpeciesName(cerSpeciesMap.get(transformListPageRspDTO.getSpeciesCode()));
                transformListPageRspDTO.setBreedName(cerBreedMap.get(transformListPageRspDTO.getBreedCode()));
            });
        }
        return rspDTOPageInfo;
    }

    @Override
    public List<TransformListByVectorTaskRspDTO> listByVectorTask(TransformListByVectorTaskReqDTO transformListByVectorTaskReqDTO) {
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskId(transformListByVectorTaskReqDTO.getVectorTaskId());
        List<TransformListByVectorTaskRspDTO> result = BeanUtils.copyListProperties(cerTransformTbList, TransformListByVectorTaskRspDTO.class);
        Map<String, String> cerSpeciesMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        Map<String, String> cerBreedMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        if (CollectionUtil.isNotEmpty(result)) {
            result.forEach(transformListByVectorTaskRspDTO -> {
                transformListByVectorTaskRspDTO.setSpeciesName(cerSpeciesMap.get(transformListByVectorTaskRspDTO.getSpeciesCode()));
                transformListByVectorTaskRspDTO.setBreedName(cerBreedMap.get(transformListByVectorTaskRspDTO.getBreedCode()));
            });
        }
        return result;
    }

    @Override
    public List<ApprovePassTransformQueryRspDTO> approvePassTransformQuery(ApprovePassTransformQueryReqDTO approvePassTransformQueryReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(approvePassTransformQueryReqDTO.getVectorTaskCode());
        List<ApprovePassTransformQueryRspDTO> list = new ArrayList<>();
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskIdAndTaskStatus(cerVectorTaskTb.getId(), BioTaskStatusEnum.TASK_STATUS_2.status);
        for (CerTransformTb cerTransformTb : cerTransformTbList) {
            if (cerVectorTaskTb == null) {
                throw new BusinessException("数据异常,转化匹配不到任务信息");
            }
            ApprovePassTransformQueryRspDTO approvePassTransformQueryRspDTO = new ApprovePassTransformQueryRspDTO();

            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(cerTransformTb.getAcceptorMaterial());
            if (cerBreedDict != null) {
                approvePassTransformQueryRspDTO.setAcceptorMaterialName(cerBreedDict.getBreedName());
            }
            approvePassTransformQueryRspDTO.setVectorTaskCode(cerTransformTb.getVectorTaskCode());
            approvePassTransformQueryRspDTO.setSubProjectCode(cerTransformTb.getSubProjectCode());
            approvePassTransformQueryRspDTO.setTransformCode(cerTransformTb.getTransformCode());
            approvePassTransformQueryRspDTO.setPlasmidName(cerTransformTb.getPlasmidName());
            approvePassTransformQueryRspDTO.setAcceptorMaterial(cerTransformTb.getAcceptorMaterial());
            list.add(approvePassTransformQueryRspDTO);
        }
        return list;
    }


}
