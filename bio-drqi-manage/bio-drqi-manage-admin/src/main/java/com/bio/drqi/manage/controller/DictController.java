package com.bio.drqi.manage.controller;

import com.bio.drqi.manage.conf.AcceptorMaterialListRspDTO;
import com.bio.drqi.manage.conf.BreedListRspDTO;
import com.bio.drqi.manage.conf.SeedProduceAddressListRsp;
import com.bio.drqi.manage.conf.SpeciesBreedListRspDTO;
import com.bio.drqi.manage.service.DictService;
import com.bio.drqi.manage.system.rsp.DictInfoRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 字典
 */
@RestController
@RequestMapping("/dict")
public class  DictController {

    @Resource
    private DictService dictService;

    /**
     * 字典
     *
     * @return
     */
    @GetMapping("/list")
    @WebLog(desc = "字典查询")
    public ResponseResult<List<DictInfoRspDTO>> list() {
        return ResponseResult.getSuccess(dictService.list());
    }





}
