package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.cer.enums.*;
import com.bio.cer.project.req.ProjectListReqDTO;
import com.bio.cer.project.rsp.*;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.DateUtil;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.service.project.ProjectService;
import com.bio.drqi.manage.service.project.SubProjectService;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private SubProjectService subProjectService;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Override
    public PageInfo<ProjectListRspDTO> listPage(ProjectListReqDTO projectListReqDTO) {
        PageHelper.startPage(projectListReqDTO.getPageNum(), projectListReqDTO.getPageSize());
        CerProjectTb selectCerProjectTb = new CerProjectTb();
        selectCerProjectTb.setProjectName(projectListReqDTO.getProjectName());
        selectCerProjectTb.setProjectCode(projectListReqDTO.getProjectCode());
        selectCerProjectTb.setSpecies(projectListReqDTO.getSpecies());
        selectCerProjectTb.setGeneEditMethod(projectListReqDTO.getGeneEditMethod());
        selectCerProjectTb.setOwnerUserId(projectListReqDTO.getOwnerUserId());
        if (projectListReqDTO.getOrder() != null) {
            selectCerProjectTb.setOrderField(projectListReqDTO.getOrder().getFieldName());
            selectCerProjectTb.setOrderType(projectListReqDTO.getOrder().getOrderType());
        }
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserById(SecurityContextHolder.getUserId());
        if(responseResult.isError()){
            throw new BusinessException(responseResult.getMessage());
        }
        List<UserDetailRspDTO.DataPermissionConfig> dataPermissionList = responseResult.getData().getDataPermissionConfigList();
        dataPermissionList = dataPermissionList.stream().filter(dataPermission -> dataPermission.getPermissionType().equals(DataPermissionTypeEnum.POC_PROJECT.name())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(dataPermissionList) && DataPermissionValueEnum.OWNER.value.equals(dataPermissionList.get(0).getPermissionValue())) {
            selectCerProjectTb.setActorId(SecurityContextHolder.getUserId());
        }
        List<CerProjectTb> cerProjectTbList = cerProjectTbMapper.selectListPage(selectCerProjectTb);
        PageInfo<CerProjectTb> srcPageInfo = new PageInfo<>(cerProjectTbList);
        PageInfo<ProjectListRspDTO> pageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, ProjectListRspDTO.class);
        if (CollectionUtil.isNotEmpty(pageInfo.getList())) {
            pageInfo.getList().forEach(projectListRspDTO -> {
                projectListRspDTO.setProjectPeriod(DateUtil.countDateNum(projectListRspDTO.getExpectStartDate(), projectListRspDTO.getExpectEndDate(), DateUtil.DATE_FORMAT_PATTERN));
                projectListRspDTO.setChildrenNum(subProjectService.list(projectListRspDTO.getId()).size());
                projectListRspDTO.setCurrentStepName(FlowStepEnum.getFlowStepNameByCode(projectListRspDTO.getCurrentStepCode()));
                projectListRspDTO.setSpeciesList(JSONUtil.toList(projectListRspDTO.getSpecies(), String.class));
            });
        }
        return pageInfo;
    }

    @Override
    public List<ListBaseInfoRspDTO> listBaseInfo() {
        List<ListBaseInfoRspDTO> result = new ArrayList<>();
        List<CerProjectTb> cerProjectTbList = cerProjectTbMapper.selectAllOrderById();
        for (CerProjectTb cerProjectTb : cerProjectTbList) {
            ListBaseInfoRspDTO listBaseInfoRspDTO = new ListBaseInfoRspDTO();
            listBaseInfoRspDTO.setProjectId(cerProjectTb.getId());
            listBaseInfoRspDTO.setProjectName(cerProjectTb.getProjectName());
            listBaseInfoRspDTO.setProjectCode(cerProjectTb.getProjectCode());
            listBaseInfoRspDTO.setProjectStatus(cerProjectTb.getProjectStatus());
            listBaseInfoRspDTO.setGeneEditMethod(cerProjectTb.getGeneEditMethod());
            listBaseInfoRspDTO.setExpectEndDate(cerProjectTb.getExpectEndDate());
            listBaseInfoRspDTO.setExpectStartDate(cerProjectTb.getExpectStartDate());
            result.add(listBaseInfoRspDTO);
        }
        return result;
    }


    @Override
    public ProjectListRspDTO detail(Integer id) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(id);
        ProjectListRspDTO projectListRspDTO = new ProjectListRspDTO();
        BeanUtil.copyProperties(cerProjectTb, projectListRspDTO);
        projectListRspDTO.setProjectPeriod(DateUtil.countDateNum(projectListRspDTO.getExpectStartDate(), projectListRspDTO.getExpectEndDate(), DateUtil.DATE_FORMAT_PATTERN));
        projectListRspDTO.setSpeciesList(JSONUtil.toList(projectListRspDTO.getSpecies(), String.class));
        return projectListRspDTO;
    }

    @Override
    public List<ProjectAllRspDTO> findAllProject() {
        List<ProjectAllRspDTO> list = new ArrayList<>();
        List<CerProjectTb> cerProjectTbList = cerProjectTbMapper.selectAll();
        if (CollectionUtil.isNotEmpty(cerProjectTbList)) {
            cerProjectTbList.forEach(cerProjectTb -> {
                ProjectAllRspDTO projectAllRspDTO = new ProjectAllRspDTO();
                projectAllRspDTO.setProjectName(cerProjectTb.getProjectName());
                projectAllRspDTO.setProjectCode(cerProjectTb.getProjectCode());
                list.add(projectAllRspDTO);
            });
        }
        return list;
    }


    @Override
    public List<ProjectUserAllRspDTO> findAllProjectAllUser() {
        List<ProjectUserAllRspDTO> result = new ArrayList<ProjectUserAllRspDTO>();
        List<CerProjectTb> cerProjectTbList = cerProjectTbMapper.selectAllByOwnerUserIdIsNotNull();
        if (CollectionUtil.isNotEmpty(cerProjectTbList)) {
            cerProjectTbList.forEach(cerProjectTb -> {
                ProjectUserAllRspDTO projectUserAllRspDTO = new ProjectUserAllRspDTO();
                projectUserAllRspDTO.setOwnerUserId(cerProjectTb.getOwnerUserId());
                projectUserAllRspDTO.setOwnerUserName(cerProjectTb.getOwnerUserName());
                result.add(projectUserAllRspDTO);

            });
        }
        return result;
    }

    @Override
    public List<ProjectSpeciesLispRspDTO> findProjectAllSpecies(String projectCode) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(projectCode);
        if (cerProjectTb == null) {
            throw new BusinessException("根据项目编号查询不到项目信息");
        }
        List<ProjectSpeciesLispRspDTO> projectSpeciesLispRspDTOS = new ArrayList<>();
        List<String> speciesList = JSONUtil.toList(cerProjectTb.getSpecies(), String.class);
        if (CollectionUtil.isNotEmpty(speciesList)) {
            speciesList.forEach(speciesCode -> {
                CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(speciesCode);
                if (cerSpeciesConf == null) {
                    throw new BusinessException("字典中缺失物种：" + speciesCode);
                }
                ProjectSpeciesLispRspDTO projectSpeciesLispRspDTO = new ProjectSpeciesLispRspDTO();
                projectSpeciesLispRspDTO.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
                projectSpeciesLispRspDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
                projectSpeciesLispRspDTOS.add(projectSpeciesLispRspDTO);
            });
        }
        return projectSpeciesLispRspDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stop(Integer id) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(id);
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("只有执行中项目可以暂停");
        }
        cerProjectTb.setProjectStatus(ProjectStatusEnum.stop.name());
        cerProjectTbMapper.updateById(cerProjectTb);

        //终止所有实施实施方案
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByProjectIdOrderById(cerProjectTb.getId());
        if (CollectionUtil.isNotEmpty(cerVectorTaskTbList)) {
            cerVectorTaskTbList.forEach(cerVectorTaskTb -> {
                cerVectorTaskTb.setTaskStatus(VectorTaskStatusEnum.TASK_STATUS_4.status);
                cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
            });
        }
        //cerProjectStatusListener.notice(ProjectStatusEnum.stop,()->cerProjectTb.getId());
    }

    @Override
    public void start(Integer id) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(id);
        if (!ProjectStatusEnum.stop.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("只有暂停项目可以再次启动");
        }
        cerProjectTb.setProjectStatus(ProjectStatusEnum.execute.name());
        cerProjectTbMapper.updateById(cerProjectTb);

        //cerProjectStatusListener.notice(ProjectStatusEnum.execute,()->cerProjectTb.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Integer id) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(id);
        cerProjectTb.setProjectStatus(ProjectStatusEnum.compete.name());
        cerProjectTbMapper.updateById(cerProjectTb);

        //完成所有实施实施方案
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByProjectIdOrderById(cerProjectTb.getId());
        if (CollectionUtil.isNotEmpty(cerVectorTaskTbList)) {
            cerVectorTaskTbList.forEach(cerVectorTaskTb -> {
                cerVectorTaskTb.setTaskStatus(VectorTaskStatusEnum.TASK_STATUS_5.status);
                cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
            });
        }

        // cerProjectStatusListener.notice(ProjectStatusEnum.compete,()->cerProjectTb.getId());

    }

}
