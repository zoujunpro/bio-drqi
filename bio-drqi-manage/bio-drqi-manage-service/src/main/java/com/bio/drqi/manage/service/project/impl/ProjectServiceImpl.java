package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.enums.*;
import com.bio.drqi.manage.project.req.ProjectListReqDTO;
import com.bio.drqi.manage.project.rsp.*;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerProjectTb;
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
        selectCerProjectTb.setGeneEditMethod(projectListReqDTO.getGeneEditMethod());
        selectCerProjectTb.setOwnerUserId(projectListReqDTO.getOwnerUserId());
        selectCerProjectTb.setId(projectListReqDTO.getId());
        selectCerProjectTb.setProjectStatus(projectListReqDTO.getProjectStatus());
        selectCerProjectTb.setProjectType(projectListReqDTO.getProjectType());
        if (projectListReqDTO.getOrder() != null) {
            selectCerProjectTb.setOrderField(projectListReqDTO.getOrder().getFieldName());
            selectCerProjectTb.setOrderType(projectListReqDTO.getOrder().getOrderType());
        }
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserById(SecurityContextHolder.getUserId());
        if (responseResult.isError()) {
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
                projectListRspDTO.setChildrenNum(subProjectService.list(projectListRspDTO.getId()).size());
                projectListRspDTO.setCurrentStepName(FlowStepEnum.getFlowStepNameByCode(projectListRspDTO.getCurrentStepCode()));
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
            listBaseInfoRspDTO.setProjectType(cerProjectTb.getProjectType());
            listBaseInfoRspDTO.setGeneEditMethod(cerProjectTb.getGeneEditMethod());
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
    @Transactional(rollbackFor = Exception.class)
    public void stop(Integer id) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(id);
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("只有执行中项目可以暂停");
        }
        cerProjectTb.setProjectStatus(ProjectStatusEnum.stop.name());
        cerProjectTbMapper.updateById(cerProjectTb);

        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByProjectId(cerProjectTb.getId());
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
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByProjectId(cerProjectTb.getId());
        if (CollectionUtil.isNotEmpty(cerVectorTaskTbList)) {
            for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
                if (VectorTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
                    throw new BusinessException("有进行中实施方案，无法完成此项目");
                }
            }
        }
        cerProjectTb.setProjectStatus(ProjectStatusEnum.compete.name());
        cerProjectTbMapper.updateById(cerProjectTb);


        // cerProjectStatusListener.notice(ProjectStatusEnum.compete,()->cerProjectTb.getId());

    }

}
