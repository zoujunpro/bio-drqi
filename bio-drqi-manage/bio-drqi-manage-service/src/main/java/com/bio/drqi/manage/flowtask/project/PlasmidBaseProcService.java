package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.manage.dto.project.PlasmidDTO;
import com.bio.drqi.manage.feign.PushAgrobacteriumToTJDBDTO;
import com.bio.drqi.mapper.CerPlasmidQualityTbMapper;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.drqi.mapper.CerSubProjectTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("plasmid_check")
@Slf4j
public class PlasmidBaseProcService extends AbstractProjectBaseTaskService {
    private static final String QUALITY_TYPE_PLASMID_PREPARE = "1";
    private static final String QUALITY_TYPE_AGROBACTERIUM_TRANSFORMATION = "2";
    private static final String QUALITY_TYPE_GRNA_SYNTHESIS = "3";

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】质粒质检校验开始");
        PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
        ValidatorUtil.validator(plasmidDTO);
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(plasmidDTO.getVectorTaskId());
        if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("不是进行中实施方案,实施方案号：" + cerVectorTaskTb.getVectorTaskCode());
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(cerVectorTaskTb.getProjectId());
        if (cerProjectTb == null) {
            throw new BusinessException("未找到项目信息 projectId=" + plasmidDTO.getProjectId());
        }
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectById(plasmidDTO.getSubProjectId());
        if (cerSubProjectTb == null) {
            throw new BusinessException("未找到子项目信息 subProjectId=" + plasmidDTO.getSubProjectId());
        }
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("非执行中项目不能进行该操作");
        }
        normalizeAndValidateContentList(plasmidDTO.getContentList());

        //补充form表单
        plasmidDTO.setProjectCode(cerProjectTb.getProjectCode());
        plasmidDTO.setProjectName(cerProjectTb.getProjectName());
        plasmidDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        plasmidDTO.setGeneEditMethod(cerProjectTb.getGeneEditMethod());
        plasmidDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plasmidDTO));
        log.info("【任务工单】质粒质检校验结束");

        /**
         * 更新当前执行步骤
         */
        logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.plasmid_check, bioTaskDtlTb.getTaskNum());
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(plasmidDTO.getVectorTaskId());
            if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
                throw new BusinessException("不是进行中实施方案,实施方案号：" + cerVectorTaskTb.getVectorTaskCode());
            }
            //更新转化质检结果
            for (PlasmidDTO.Content content : plasmidDTO.getContentList()) {
                CerPlasmidQualityTb cerPlasmidQualityTb = new CerPlasmidQualityTb();
                cerPlasmidQualityTb.setSubProjectId(plasmidDTO.getSubProjectId());
                cerPlasmidQualityTb.setProjectId(plasmidDTO.getProjectId());
                cerPlasmidQualityTb.setVectorTaskId(plasmidDTO.getVectorTaskId());
                cerPlasmidQualityTb.setPlasmidName(content.getPlasmidName());
                cerPlasmidQualityTb.setQualityInspectionNumber(content.getPlasmidName());
                cerPlasmidQualityTb.setQualityInspectionResult(content.getQualityInspectionResult());
                cerPlasmidQualityTb.setAgrobacteriumInformation(content.getAgrobacteriumInformation());
                cerPlasmidQualityTb.setCreateUserName(SecurityContextHolder.getNickName());
                cerPlasmidQualityTb.setCreateUserId(SecurityContextHolder.getUserId());
                cerPlasmidQualityTb.setUpdateTime(new Date());
                cerPlasmidQualityTb.setCreateTime(new Date());
                cerPlasmidQualityTb.setQualityInspectionType(content.getQualityInspectionType());
                cerPlasmidQualityTb.setAgrobacteriumResistance(content.getAgrobacteriumResistance());
                cerPlasmidQualityTb.setPlasmidConcentration(content.getPlasmidConcentration());
                cerPlasmidQualityTb.setExtractionKit(content.getExtractionKit());
                cerPlasmidQualityTb.setGrnaSequence(content.getGrnaSequence());
                cerPlasmidQualityTb.setTaskStatus(bioTaskDtlTb.getTaskStatus());
                cerPlasmidQualityTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                cerPlasmidQualityTb.setFileUrls(JSONUtil.toJsonStr(content.getFileUrlList()));
                cerPlasmidQualityTb.setProjectCode(cerVectorTaskTb.getProjectCode());
                cerPlasmidQualityTb.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
                cerPlasmidQualityTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                cerPlasmidQualityTb.setRemark(content.getRemark());
                cerPlasmidQualityTb.setAgrobacteriumLocation(content.getAgrobacteriumLocation());
                cerPlasmidQualityTb.setMakingDate(content.getMakingDate());
                cerPlasmidQualityTbMapper.insert(cerPlasmidQualityTb);
            }
            pushAgrobacteriumToTJDB(plasmidDTO.getContentList());
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        cerPlasmidQualityTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
        cerVectorStepLogMapper.deleteByTaskNumAndStepCode(bioTaskDtlTb.getTaskNum(), ImplementationPlanTypeEnum.plasmid_check.name());
    }


    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        PlasmidDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();

        List<BioHtmlModelDTO.ModelField> fieldList = new ArrayList<>();
        fieldList.add(buildField("项目名称", dto.getProjectName()));
        fieldList.add(buildField("项目编号", dto.getProjectCode()));
        fieldList.add(buildField("子项目编号", dto.getSubProjectCode()));
        fieldList.add(buildField("实施方案编号", dto.getVectorTaskCode()));
        sections.add(buildFieldSection("申请信息", fieldList));

        if (CollectionUtil.isNotEmpty(dto.getContentList())) {
            List<String> headers = Arrays.asList("质粒名称", "下一步安排", "质检结果", "农杆菌信息", "农杆菌抗性", "质粒浓度", "提取试剂盒", "gRNA序列", "储存位置", "农杆菌制备时间", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (PlasmidDTO.Content item : dto.getContentList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("质粒名称", item.getPlasmidName());
                row.put("下一步安排", qualityInspectionTypeName(item.getQualityInspectionType()));
                row.put("质检结果", qualityInspectionResultName(item.getQualityInspectionResult()));
                row.put("农杆菌信息", item.getAgrobacteriumInformation());
                row.put("农杆菌抗性", item.getAgrobacteriumResistance());
                row.put("质粒浓度", item.getPlasmidConcentration());
                row.put("提取试剂盒", item.getExtractionKit());
                row.put("gRNA序列", item.getGrnaSequence());
                row.put("储存位置", item.getAgrobacteriumLocation());
                row.put("农杆菌制备时间", item.getMakingDate());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("质粒质检明细", headers, rows));
        }

        return sections;
    }

    private void normalizeAndValidateContentList(List<PlasmidDTO.Content> contentList) {
        if (CollectionUtil.isEmpty(contentList)) {
            throw new BusinessException("质检信息缺失");
        }
        for (PlasmidDTO.Content content : contentList) {
            String qualityInspectionType = normalizeQualityInspectionType(content.getQualityInspectionType());
            if (StrUtil.isBlank(qualityInspectionType)) {
                throw new BusinessException("质检类型非法: 1质粒制备 2农杆菌转化 3gRNA合成");
            }
            content.setQualityInspectionType(qualityInspectionType);
            if (QUALITY_TYPE_GRNA_SYNTHESIS.equals(qualityInspectionType) && StrUtil.isBlank(content.getGrnaSequence())) {
                throw new BusinessException("下一步安排为gRNA合成时，gRNA序列不能为空，质粒名称：" + content.getPlasmidName());
            }
        }
    }

    private String normalizeQualityInspectionType(String qualityInspectionType) {
        if (StrUtil.isBlank(qualityInspectionType)) {
            return null;
        }
        String type = qualityInspectionType.trim();
        if (QUALITY_TYPE_PLASMID_PREPARE.equals(type) || "质粒制备".equals(type)) {
            return QUALITY_TYPE_PLASMID_PREPARE;
        }
        if (QUALITY_TYPE_AGROBACTERIUM_TRANSFORMATION.equals(type)
                || "农杆菌检测".equals(type)
                || "农杆菌转化".equals(type)) {
            return QUALITY_TYPE_AGROBACTERIUM_TRANSFORMATION;
        }
        if (QUALITY_TYPE_GRNA_SYNTHESIS.equals(type) || "gRNA合成".equals(type)) {
            return QUALITY_TYPE_GRNA_SYNTHESIS;
        }
        return null;
    }

    private String qualityInspectionTypeName(String code) {
        if (QUALITY_TYPE_PLASMID_PREPARE.equals(code)) {
            return "质粒制备";
        }
        if (QUALITY_TYPE_AGROBACTERIUM_TRANSFORMATION.equals(code)) {
            return "农杆菌转化";
        }
        if (QUALITY_TYPE_GRNA_SYNTHESIS.equals(code)) {
            return "gRNA合成";
        }
        return code;
    }

    private void pushAgrobacteriumToTJDB(List<PlasmidDTO.Content> contentList) {
        if (CollectionUtil.isEmpty(contentList)) {
            return;
        }
        List<PushAgrobacteriumToTJDBDTO> agrobacteriumList = new ArrayList<>();
        for (PlasmidDTO.Content content : contentList) {
            if (!isAgrobacteriumCheck(content.getQualityInspectionType())) {
                continue;
            }
            agrobacteriumList.add(buildPushAgrobacteriumToTJDBDTO(content));
        }
        if (CollectionUtil.isEmpty(agrobacteriumList)) {
            return;
        }
        doPushAgrobacteriumToTJDB(agrobacteriumList);
    }

    private boolean isAgrobacteriumCheck(String qualityInspectionType) {
        return QUALITY_TYPE_AGROBACTERIUM_TRANSFORMATION.equals(qualityInspectionType)
                || "农杆菌检测".equals(qualityInspectionType)
                || "农杆菌转化".equals(qualityInspectionType);
    }

    private PushAgrobacteriumToTJDBDTO buildPushAgrobacteriumToTJDBDTO(PlasmidDTO.Content content) {
        PushAgrobacteriumToTJDBDTO request = new PushAgrobacteriumToTJDBDTO();
        request.setPlasmidID(content.getPlasmidName());
        request.setLocal(content.getAgrobacteriumLocation());
        request.setResistance(defaultNA(content.getAgrobacteriumResistance()));
        request.setStrain(defaultNA(content.getAgrobacteriumInformation()));
        request.setSupplement(defaultNA(content.getRemark()));
        request.setMaking_date(defaultNA(content.getMakingDate()));
        request.setTemid("1");
        return request;
    }

    private void doPushAgrobacteriumToTJDB(List<PushAgrobacteriumToTJDBDTO> agrobacteriumList) {
        String url = "http://172.16.14.2:10091/PushAgrobacteriumToTJDB";
        Map<String, Object> map = new HashMap<>();
        map.put("jobNum", SecurityContextHolder.getJobNum());
        map.put("nickname", SecurityContextHolder.getNickName());
        map.put("source", "drqi");
        map.put("AgrobacteriumList", agrobacteriumList);

        String requestBody = JSONUtil.toJsonStr(map);
        log.info("【农杆菌信息储存】调用接口开始，url={}, request={}", url, requestBody);
        HttpResponse httpResponse = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .execute();
        String response = httpResponse.body();
        log.info("【农杆菌信息储存】调用接口结束，status={}, response={}", httpResponse.getStatus(), response);
        if (!httpResponse.isOk()) {
            throw new BusinessException("农杆菌信息储存失败：接口返回HTTP状态码" + httpResponse.getStatus());
        }
        JSONObject responseJson = JSONUtil.parseObj(response);
        JSONArray data = responseJson.getJSONArray("data");
        if (CollectionUtil.isEmpty(data)) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            JSONObject item = data.getJSONObject(i);
            String errorLog = item.getStr("Errorlog");
            if (StrUtil.isNotBlank(errorLog)) {
                throw new BusinessException("农杆菌信息储存失败：" + errorLog);
            }
        }
    }

    private String defaultNA(String value) {
        return StrUtil.isBlank(value) ? "NA" : value;
    }

    private String qualityInspectionResultName(String code) {
        if ("pass".equals(code)) {
            return "合格";
        }
        if ("refuse".equals(code)) {
            return "不合格";
        }
        return code;
    }
}
