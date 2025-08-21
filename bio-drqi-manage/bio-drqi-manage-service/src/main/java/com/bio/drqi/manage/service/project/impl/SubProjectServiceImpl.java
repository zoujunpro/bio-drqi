package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.CerSubProjectTb;
import com.bio.drqi.manage.service.project.SubProjectService;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.CerSubProjectTbMapper;
import com.bio.drqi.manage.project.rsp.ProjectSpeciesLispRspDTO;
import com.bio.drqi.manage.project.rsp.SubProjectRspDTO;
import com.bio.common.core.dto.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SubProjectServiceImpl  implements SubProjectService {

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Override
    public List<SubProjectRspDTO> list(Integer projectId) {
        List<SubProjectRspDTO> subProjectRspDTOList = new ArrayList<>();
        List<CerSubProjectTb> cerSubProjectTbList = cerSubProjectTbMapper.selectAllByProjectId(projectId);
        for (CerSubProjectTb cerSubProjectTb : cerSubProjectTbList) {
            SubProjectRspDTO subProjectRspDTO = new SubProjectRspDTO();
            subProjectRspDTO.setId(cerSubProjectTb.getId());
            subProjectRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
            subProjectRspDTO.setFileUrls(JSONUtil.toList(cerSubProjectTb.getFileUrls(), String.class));
            subProjectRspDTO.setTaskNum(cerSubProjectTb.getTaskNum());
            subProjectRspDTO.setCreateUserId(cerSubProjectTb.getCreateUserId());
            subProjectRspDTO.setCreateUserName(cerSubProjectTb.getCreateUserName());
            subProjectRspDTO.setTaskStatus(cerSubProjectTb.getTaskStatus());
            subProjectRspDTO.setPriorityLevel(cerSubProjectTb.getPriorityLevel());
            subProjectRspDTO.setSpeciesList(JSONUtil.toList(cerSubProjectTb.getSpeciesCode(),String.class));
            subProjectRspDTOList.add(subProjectRspDTO);
        }
        return subProjectRspDTOList;
    }

    @Override
    public List<ProjectSpeciesLispRspDTO> findSubProjectAllSpecies(String subProjectCode) {
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(subProjectCode);
        if (cerSubProjectTb == null) {
            throw new BusinessException("子项目不存在");
        }
        List<ProjectSpeciesLispRspDTO> projectSpeciesLispRspDTOS=new ArrayList<>();
        List<String> speciesList = JSONUtil.toList(cerSubProjectTb.getSpeciesCode(), String.class);
        if (CollectionUtil.isNotEmpty(speciesList)) {
            speciesList.forEach(speciesCode -> {
                CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(speciesCode);
                if(cerSpeciesConf==null){
                    throw new BusinessException("字典中缺失物种："+speciesCode);
                }
                ProjectSpeciesLispRspDTO projectSpeciesLispRspDTO=new ProjectSpeciesLispRspDTO();
                projectSpeciesLispRspDTO.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
                projectSpeciesLispRspDTOS.add(projectSpeciesLispRspDTO);
            });
        }
        return projectSpeciesLispRspDTOS;
    }




}
