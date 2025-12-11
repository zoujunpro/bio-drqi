package com.bio.drqi.manage.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.devOps.DevOpsModifyProjectCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifySubProjectCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifyVectorTaskCodeBreedCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifyVectorTaskCodeReqDTO;
import com.bio.drqi.manage.dto.plant.task.PlantExperimentTaskDTO;
import com.bio.drqi.manage.service.DevOpsService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/devOpsTest")
@Slf4j
public class DevOpsController {

    @Resource
    private DevOpsService devOpsService;


    /**
     * 更改实施方案的品种信息
     *
     * @param devOpsModifyVectorTaskCodeBreedCodeReqDTO
     * @return
     */
    @PostMapping("modifyVectorTaskCodeBreedCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> modifyVectorTaskCodeBreedCode(@RequestBody DevOpsModifyVectorTaskCodeBreedCodeReqDTO devOpsModifyVectorTaskCodeBreedCodeReqDTO) {
        devOpsService.modifyVectorTaskCodeBreedCode(devOpsModifyVectorTaskCodeBreedCodeReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 清洗子项目编号
     *
     * @param devOpsModifySubProjectCodeReqDTO
     * @return
     */
    @PostMapping("/cleanSubProjectCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSubProjectCode(@RequestBody DevOpsModifySubProjectCodeReqDTO devOpsModifySubProjectCodeReqDTO) {
        devOpsService.cleanSubProjectCode(devOpsModifySubProjectCodeReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 清洗实施方案编号
     *
     * @param devOpsModifySubProjectCodeReqDTO
     * @return
     */
    @PostMapping("/cleanVectorTaskCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVectorTaskCode(@RequestBody DevOpsModifyVectorTaskCodeReqDTO devOpsModifySubProjectCodeReqDTO) {
        devOpsService.cleanVectorTaskCode(devOpsModifySubProjectCodeReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 清洗项目编号
     *
     * @param devOpsModifyProjectCodeReqDTO
     * @return
     */
    @PostMapping("/cleanProjectCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanProjectCode(@RequestBody DevOpsModifyProjectCodeReqDTO devOpsModifyProjectCodeReqDTO) {
        devOpsService.cleanProjectCode(devOpsModifyProjectCodeReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 根据项目编号删除整条项目数据
     *
     * @param projectCode
     * @return
     */
    @GetMapping("/deleteByProjectCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> deleteByProjectCode(@RequestParam String projectCode) {
        devOpsService.deleteByProjectCode(projectCode);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 根据项实施方案编号删除子项目信息
     *
     * @param vectorTaskCode
     * @return
     */
    @GetMapping("/deleteByVectorTaskCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> deleteByVectorTaskCode(@RequestParam String vectorTaskCode) {
        devOpsService.deleteByVectorTaskCode(vectorTaskCode);
        return ResponseResult.getSuccess("ok");
    }

    public static void main(String[] args) {
        System.out.println("TC001-04");
    }
}
