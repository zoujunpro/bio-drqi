package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.drqi.board.*;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.drqi.domain.CerConversionAndTransTb;
import com.bio.drqi.domain.CerSampleTestTb;
import com.bio.drqi.domain.CerVectorStepLog;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.service.project.ProjectBoardService;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectBoardServiceImpl implements ProjectBoardService {

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;


    @Override
    public ProjectTaskCountRspDTO taskCount() {
        ProjectTaskCountRspDTO projectTaskCountRspDTO = new ProjectTaskCountRspDTO();
        projectTaskCountRspDTO.setTotalCountNum(bioTaskDtlTbMapper.selectCountALl("project"));
        projectTaskCountRspDTO.setApplyCountNum(bioTaskDtlTbMapper.selectCountMyApprove(SecurityContextHolder.getUserId(),"project"));
        projectTaskCountRspDTO.setDealCountNum(bioTaskDtlTbMapper.selectForAlreadyApprovalCount(String.valueOf(SecurityContextHolder.getUserId()),"project"));
        projectTaskCountRspDTO.setPendingCountNum(bioTaskDtlTbMapper.selectForPendingApprovalCount(String.valueOf(SecurityContextHolder.getUserId()),"project"));
        projectTaskCountRspDTO.setProjectCountNum(cerProjectTbMapper.selectAllCount());
        Integer conversionAndTransCountNum = cerConversionAndTransTbMapper.selectCountNum();
        projectTaskCountRspDTO.setConversionAndTransCountNum(conversionAndTransCountNum == null ? 0 : conversionAndTransCountNum);
        Integer transFormCountNum = cerTransformTbMapper.selectSumInfectNumber();
        projectTaskCountRspDTO.setVectorTaskCountNum(cerVectorTaskTbMapper.selectCountNum());
        projectTaskCountRspDTO.setSampleCountNum(cerSampleTestTbMapper.selectCountNum());
        projectTaskCountRspDTO.setTransFormCountNum(transFormCountNum==null?0:transFormCountNum);
        return projectTaskCountRspDTO;
    }

    @Override
    public List<CountTransByMonthRspDTO> countTransByMonth(String year) {
        List<CerConversionAndTransTb> cerConversionAndTransTbList = cerConversionAndTransTbMapper.selectCountTransNumByMonth(year);
        Map<String, Integer> transDateAndNumMap = cerConversionAndTransTbList.stream().collect(Collectors.toMap(CerConversionAndTransTb::getTransDate, CerConversionAndTransTb::getTransNumber));
        List<CountTransByMonthRspDTO> countTransByMonthRspDTOList = new ArrayList<>();
        List<String> monthList = createMonthList(year);
        monthList.forEach(month -> {
            CountTransByMonthRspDTO countTransByMonthRspDTO = new CountTransByMonthRspDTO();
            countTransByMonthRspDTO.setTransDate(month);
            countTransByMonthRspDTO.setTransNumber(transDateAndNumMap.get(month) == null ? 0 : transDateAndNumMap.get(month));
            countTransByMonthRspDTOList.add(countTransByMonthRspDTO);
        });

        return countTransByMonthRspDTOList;
    }

    @Override
    public List<CountSampleByMonthRspDTO> countSampleByMonth(String year) {
        List<CountSampleByMonthRspDTO> countSampleByMonthRspDTOList = new ArrayList<>();
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectCountByMonth(year);
        Map<String, Integer> sqlMap = cerSampleTestTbList.stream().collect(Collectors.toMap(CerSampleTestTb::getSampleMonth, CerSampleTestTb::getCountNum));
        List<String> monthList = createMonthList(year);
        monthList.forEach(month -> {
            CountSampleByMonthRspDTO countTransByMonthRspDTO = new CountSampleByMonthRspDTO();
            countTransByMonthRspDTO.setSampleDate(month);
            countTransByMonthRspDTO.setSampleNum(sqlMap.get(month) == null ? 0 : sqlMap.get(month));
            countSampleByMonthRspDTOList.add(countTransByMonthRspDTO);
        });
        return countSampleByMonthRspDTOList;
    }

    @Override
    public List<VectorTaskListBoardRspDTO> vectorTaskListBoard(VectorTaskListBoardReqDTO vectorTaskListBoardReqDTO) {
        List<VectorTaskListBoardRspDTO> vectorTaskListBoardRspDTOList = new ArrayList<>();
        List<CerVectorStepLog> cerVectorStepLogList = cerVectorStepLogMapper.selectList(null);
        Map<Integer, List<CerVectorStepLog>> cerVectorStepLogListMap = cerVectorStepLogList.stream().collect(Collectors.groupingBy(CerVectorStepLog::getVectorTaskId));
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllForBoard(vectorTaskListBoardReqDTO.getUserId(), vectorTaskListBoardReqDTO.getProjectId(), vectorTaskListBoardReqDTO.getSpeciesCode(),vectorTaskListBoardReqDTO.getTaskStatus());
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            VectorTaskListBoardRspDTO vectorTaskListBoardRspDTO = new VectorTaskListBoardRspDTO();
            vectorTaskListBoardRspDTO.setProjectId(cerVectorTaskTb.getProjectId());
            vectorTaskListBoardRspDTO.setProjectCode(cerVectorTaskTb.getProjectCode());
            vectorTaskListBoardRspDTO.setVectorTaskId(cerVectorTaskTb.getId());
            vectorTaskListBoardRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
            vectorTaskListBoardRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
            List<CerVectorStepLog> vectorStepList = cerVectorStepLogListMap.get(cerVectorTaskTb.getId()) == null ? new ArrayList<>() : cerVectorStepLogListMap.get(cerVectorTaskTb.getId());
            List<String> stepCodeList = vectorStepList.stream().map(CerVectorStepLog::getStepCode).collect(Collectors.toList());
            for (ImplementationPlanTypeEnum implementationPlanTypeEnum : ImplementationPlanTypeEnum.values()) {
                vectorTaskListBoardRspDTO.buildStepList(implementationPlanTypeEnum.name(), implementationPlanTypeEnum.desc, CollectionUtil.isNotEmpty(stepCodeList) && stepCodeList.contains(implementationPlanTypeEnum.name()) ? CerProjectContents.Y : CerProjectContents.N);
            }
            vectorTaskListBoardRspDTOList.add(vectorTaskListBoardRspDTO);
        }
        return vectorTaskListBoardRspDTOList;
    }


    private List<String> createMonthList(String year) {
        List<String> list = new ArrayList<>();
        list.add(year + "-01");
        list.add(year + "-02");
        list.add(year + "-03");
        list.add(year + "-04");
        list.add(year + "-05");
        list.add(year + "-06");
        list.add(year + "-07");
        list.add(year + "-08");
        list.add(year + "-09");
        list.add(year + "-10");
        list.add(year + "-11");
        list.add(year + "-12");
        return list;
    }

}
