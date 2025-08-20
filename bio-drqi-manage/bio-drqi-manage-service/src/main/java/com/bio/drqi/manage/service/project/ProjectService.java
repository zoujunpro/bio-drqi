package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.project.req.ProjectListReqDTO;
import com.bio.drqi.manage.project.rsp.*;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ProjectService {

    /**
     * 分页查询项目列表
     */

    PageInfo<ProjectListRspDTO> listPage(ProjectListReqDTO projectListReqDTO);

    List<ListBaseInfoRspDTO> listBaseInfo();

    /**
     * 根据主键查询项目详情信息
     */
    ProjectListRspDTO detail(Integer id);

    List<ProjectAllRspDTO> findAllProject();

    List<ProjectUserAllRspDTO> findAllProjectAllUser();

    void stop(Integer id);

    void start(Integer id);

    void complete(Integer id);

}
