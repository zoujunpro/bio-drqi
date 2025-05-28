package com.bio.drqi.manage.freemarker.service.impl;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.GenerationEnum;
import com.bio.drqi.enums.SeedTaskTypeEnum;

import com.bio.drqi.manage.dto.seed.SeedDestructionDTO;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.dto.seed.SeedOutDTO;
import com.bio.drqi.manage.freemarker.dto.HtmlGenerateDTO;
import com.bio.drqi.manage.freemarker.handle.HtmlToPDFHandle;
import com.bio.drqi.manage.freemarker.service.SeedPdfService;
import com.bio.drqi.manage.service.DictInnerService;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;

import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.freemarker.util.HtmlToPdfUtils;
import com.bio.flow.dto.ApproveDetailRspDTO;
import com.bio.flow.service.FlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SeedPdfServiceImpl implements SeedPdfService {


    @Value("${spring.freemarker.fontPath}")
    private String fontPath;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private FlowService flowService;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private DictInnerService dictInnerService;

    @Override
    public void generatePDF(Integer taskId, HttpServletResponse httpServletResponse) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(taskId);
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserById(bioTaskDtlTb.getApplyUserId());
        if (responseResult.isError()) {
            throw new BusinessException("申请用户信息不存在");
        }
        try {
            HtmlGenerateDTO htmlGenerateDTO = new HtmlGenerateDTO();
            htmlGenerateDTO.setPrintTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
            htmlGenerateDTO.setPrintUser(SecurityContextHolder.getNickName());
            htmlGenerateDTO.setTaskDesc(bioTaskDtlTb.getTaskDesc());
            htmlGenerateDTO.setTaskNum(bioTaskDtlTb.getTaskNum());
            htmlGenerateDTO.setTaskType(bioTaskDtlTb.getTaskTypeName());
            htmlGenerateDTO.setApplyName(bioTaskDtlTb.getApplyUserName());
            htmlGenerateDTO.setApplyDate(DateUtil.format(bioTaskDtlTb.getApplyTime(), DatePattern.NORM_DATE_PATTERN));
            htmlGenerateDTO.setDeptName(responseResult.getData().getDeptName());
            htmlGenerateDTO.setApproveResult(BioTaskStatusEnum.getNameByStatus(bioTaskDtlTb.getTaskStatus()));
            ApproveDetailRspDTO approveDetailRspDTO = flowService.approveDetail(String.valueOf(bioTaskDtlTb.getInstanceId()));
            htmlGenerateDTO.setContentData(transTaskFormToContentData(bioTaskDtlTb.getTaskForm(), bioTaskDtlTb.getTaskTypeCode()));
            htmlGenerateDTO.setNodeList(htmlGenerateDTO.buildNodeList(approveDetailRspDTO).getNodeList());
            String html = HtmlToPDFHandle.generateHtml(htmlGenerateDTO, bioTaskDtlTb.getTaskTypeCode());
            HtmlToPdfUtils.html2Pdf(IoUtil.toStream(html, "UTF-8"), httpServletResponse, bioTaskDtlTb.getTaskTypeName()+"申请单",fontPath);
            System.out.println(html);
        } catch (Exception e) {
            log.error("系统生成PDF失败", e);
            throw new BusinessException("系统生成PDF失败");
        }
    }

    private Object transTaskFormToContentData(String taskForm, String taskType) {
        if (SeedTaskTypeEnum.seed_store_apply.name().equals(taskType)) {
            SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(taskForm, SeedInStoreDTO.class);
            translateSeedInStoreDTO(seedInStoreDTO);
            return seedInStoreDTO;
        } else if (SeedTaskTypeEnum.seed_destruction_apply.name().equals(taskType)) {
            List<SeedDestructionDTO> seedDestructionDTOList = JSONUtil.toList(taskForm, SeedDestructionDTO.class);
            return seedDestructionDTOList;
        } else if (SeedTaskTypeEnum.seed_out_apply.name().equals(taskType)) {
            List<SeedOutDTO> seedOutDTOList = JSONUtil.toList(taskForm, SeedOutDTO.class);
            return seedOutDTOList;
        }
        return null;
    }


    private void translateSeedInStoreDTO(SeedInStoreDTO seedInStoreDTO) {
        for (SeedInStoreDTO.ExecuteFormContent executeFormContent : seedInStoreDTO.getExecuteForm().getExecuteFormContentList()) {
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCodeAndSpeciesCode(executeFormContent.getBreedCode(),executeFormContent.getSpeciesCode());
            if (cerBreedDict != null) {
                executeFormContent.setBreedCode(cerBreedDict.getBreedName());
            }
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(executeFormContent.getSpeciesCode());
            if (cerSpeciesConf != null) {
                executeFormContent.setSpeciesName(cerSpeciesConf.getSpeciesName());
            }
            executeFormContent.setGeneration(GenerationEnum.getGenerationDesc(executeFormContent.getGeneration()));
            executeFormContent.setSource(dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.SOURCE_CHANNEL,executeFormContent.getSource()).getDictValueName());
        }
    }
}
