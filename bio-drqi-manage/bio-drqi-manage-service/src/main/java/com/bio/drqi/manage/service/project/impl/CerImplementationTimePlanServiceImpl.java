package com.bio.drqi.manage.service.project.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.domain.CerSubProjectTb;
import com.bio.drqi.enums.VectorTaskPlanEventTypeEnum;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.drqi.mapper.CerSubProjectTbMapper;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanAddReqDTO;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanExportDTO;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanExportReqDTO;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanListRspDTO;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerVectorTaskPlanLog;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.service.project.CerImplementationTimePlanService;
import com.bio.drqi.mapper.CerVectorTaskPlanLogMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CerImplementationTimePlanServiceImpl implements CerImplementationTimePlanService {

    @Resource
    private CerVectorTaskPlanLogMapper cerVectorTaskPlanLogMapper;

    @Resource
    private CerVectorTaskTbMapper vectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private OssService ossService;

    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;


    @Override
    public VectorTaskTimePlanListRspDTO list(String vectorTaskCode) {
        VectorTaskTimePlanListRspDTO vectorTaskTimePlanListRspDTO = new VectorTaskTimePlanListRspDTO();
        CerVectorTaskTb cerVectorTaskTb = vectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb != null) {
            List<CerVectorTaskPlanLog> cerVectorTaskPlanLogList = cerVectorTaskPlanLogMapper.selectAllByVectorTaskIdOrderByIdAsc(cerVectorTaskTb.getId());
            List<VectorTaskTimePlanListRspDTO.Content> contentList = BeanUtils.copyToList(cerVectorTaskPlanLogList, VectorTaskTimePlanListRspDTO.Content.class);
            vectorTaskTimePlanListRspDTO.setContentList(contentList);
            return vectorTaskTimePlanListRspDTO.buildOverTimeFlag().countEstimatedTotalDay();
        } else {
            return new VectorTaskTimePlanListRspDTO();
        }

    }

    @Override
    public void add(VectorTaskTimePlanAddReqDTO vectorTaskTimePlanAddReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = vectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskTimePlanAddReqDTO.getVectorTaskCode());
        CerVectorTaskPlanLog cerVectorTaskPlanLog = new CerVectorTaskPlanLog();
        cerVectorTaskPlanLog.setVectorTaskId(cerVectorTaskTb.getId());
        cerVectorTaskPlanLog.setEventType(vectorTaskTimePlanAddReqDTO.getEventType());
        cerVectorTaskPlanLog.setEstimatedStartTime(vectorTaskTimePlanAddReqDTO.getEstimatedStartTime());
        cerVectorTaskPlanLog.setEstimatedEndTime(vectorTaskTimePlanAddReqDTO.getEstimatedEndTime());
        cerVectorTaskPlanLog.setUserId(SecurityContextHolder.getUserId());
        cerVectorTaskPlanLog.setUserName(SecurityContextHolder.getNickName());
        cerVectorTaskPlanLog.setCreateTime(new Date());
        cerVectorTaskPlanLogMapper.insert(cerVectorTaskPlanLog);
    }

    @Override
    public void exportExcel(VectorTaskTimePlanExportReqDTO vectorTaskTimePlanExportReqDTO, HttpServletResponse httpServletResponse) {
        List<CerVectorTaskPlanLog> cerVectorTaskPlanLogList = new ArrayList<>();
        if (CollectionUtil.isEmpty(vectorTaskTimePlanExportReqDTO.getVectorTaskCodeList())) {
            cerVectorTaskPlanLogList = cerVectorTaskPlanLogMapper.selectAll();
        } else {
            List<CerVectorTaskTb> cerVectorTaskTbList = vectorTaskTbMapper.selectAllByVectorTaskCodeIn(vectorTaskTimePlanExportReqDTO.getVectorTaskCodeList());
            cerVectorTaskPlanLogList = cerVectorTaskPlanLogMapper.selectAllByVectorTaskIdIn(cerVectorTaskTbList.stream().map(CerVectorTaskTb::getId).collect(Collectors.toList()));
        }
        List<CerProjectTb> cerProjectTbList = cerProjectTbMapper.selectAll();
        Map<String, CerProjectTb> cerProjectTbMap = cerProjectTbList.stream().collect(Collectors.toMap(CerProjectTb::getProjectCode, cerProjectTb -> cerProjectTb));

        List<CerSubProjectTb> cerSubProjectTbList = cerSubProjectTbMapper.selectList(null);
        Map<String, CerSubProjectTb> cerSubProjectTbMap = cerSubProjectTbList.stream().collect(Collectors.toMap(CerSubProjectTb::getSubProjectCode, cerSubProjectTb -> cerSubProjectTb));

        List<CerVectorTaskTb> cerVectorTaskTbList = vectorTaskTbMapper.selectList(null);
        Map<Integer, CerVectorTaskTb> cerVectorTaskTbMap = cerVectorTaskTbList.stream().collect(Collectors.toMap(CerVectorTaskTb::getId, cerVectorTaskTb -> cerVectorTaskTb));

        List<VectorTaskTimePlanExportDTO> vectorTaskTimePlanExportDTOList = new ArrayList<VectorTaskTimePlanExportDTO>();
        for (CerVectorTaskPlanLog vectorTaskPlanLog : cerVectorTaskPlanLogList) {
            VectorTaskTimePlanExportDTO vectorTaskTimePlanExportDTO = new VectorTaskTimePlanExportDTO();
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMap.get(vectorTaskPlanLog.getVectorTaskId());
            if (cerVectorTaskTb == null) {
                continue;
            }
            vectorTaskTimePlanExportDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
            vectorTaskTimePlanExportDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
            vectorTaskTimePlanExportDTO.setProjectCode(cerProjectTbMap.get(cerVectorTaskTb.getProjectCode()).getProjectCode());
            vectorTaskTimePlanExportDTO.setProjectName(cerProjectTbMap.get(cerVectorTaskTb.getProjectCode()).getProjectName());
            vectorTaskTimePlanExportDTO.setSubProjectCode(cerSubProjectTbMap.get(cerVectorTaskTb.getSubProjectCode()).getSubProjectCode());
            vectorTaskTimePlanExportDTO.setUserName(vectorTaskPlanLog.getUserName());
            vectorTaskTimePlanExportDTO.setEventTypeName(VectorTaskPlanEventTypeEnum.getDescByCode(vectorTaskPlanLog.getEventType()));
            vectorTaskTimePlanExportDTO.setEstimatedStartTime(vectorTaskPlanLog.getEstimatedStartTime());
            vectorTaskTimePlanExportDTO.setEstimatedEndTime(vectorTaskPlanLog.getEstimatedEndTime());
            vectorTaskTimePlanExportDTO.setActualStartTime(vectorTaskPlanLog.getActualStartTime());
            vectorTaskTimePlanExportDTO.setActualEndTime(vectorTaskPlanLog.getActualEndTime());

            if (StringUtils.isNotEmpty(vectorTaskTimePlanExportDTO.getEstimatedStartTime()) && StringUtils.isNotEmpty(vectorTaskTimePlanExportDTO.getEstimatedEndTime())) {
                vectorTaskTimePlanExportDTO.setEstimatedDate(DateUtil.betweenDay(DateUtil.parse(vectorTaskTimePlanExportDTO.getEstimatedStartTime(), "yyyy-MM-dd"), DateUtil.parse(vectorTaskTimePlanExportDTO.getEstimatedEndTime(), "yyyy-MM-dd"), true) + 1L);
            }
            if (StringUtils.isNotEmpty(vectorTaskTimePlanExportDTO.getActualStartTime()) && StringUtils.isNotEmpty(vectorTaskTimePlanExportDTO.getActualEndTime())) {
                vectorTaskTimePlanExportDTO.setActualDate(DateUtil.betweenDay(DateUtil.parse(vectorTaskTimePlanExportDTO.getActualStartTime(), "yyyy-MM-dd"), DateUtil.parse(vectorTaskTimePlanExportDTO.getActualEndTime(), "yyyy-MM-dd"), true) + 1L);
            }
            vectorTaskTimePlanExportDTOList.add(vectorTaskTimePlanExportDTO);
        }
        //导出excel
        String excelTemplateName = "实时方案预估时间模板V1.0.xlsx";
        String templateDir = System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator + excelTemplateName;
        try {
            ossService.downloadPath(templateDir, excelTemplatePath, excelTemplateName);
        } catch (Exception e) {
            throw new BusinessException("实时方案预估时间模板V1下载模板异常");
        }
        ExcelUtil.fillExcel(templateDir, vectorTaskTimePlanExportDTOList, VectorTaskTimePlanExportDTO.class, httpServletResponse);

    }
}
