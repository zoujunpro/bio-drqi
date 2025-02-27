package com.bio.drqi.manage.service.project.impl;

import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.transform.req.ApprovePassTransformQueryReqDTO;
import com.bio.drqi.transform.req.TransformListByVectorTaskReqDTO;
import com.bio.drqi.transform.req.TransformListByVectorTaskRspDTO;
import com.bio.drqi.transform.rsp.ApprovePassTransformQueryRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerTransformTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.service.project.TransformService;
import com.bio.drqi.mapper.CerTransformTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TransformServiceImpl implements TransformService {


    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;


    @Override
    public List<TransformListByVectorTaskRspDTO> listByVectorTask(TransformListByVectorTaskReqDTO transformListByVectorTaskReqDTO) {
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskId(transformListByVectorTaskReqDTO.getVectorTaskId());
        return BeanUtils.copyListProperties(cerTransformTbList, TransformListByVectorTaskRspDTO.class);
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
