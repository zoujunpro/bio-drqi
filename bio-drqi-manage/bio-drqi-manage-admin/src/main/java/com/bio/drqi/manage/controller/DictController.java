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


    /**
     * 获取所有种子地址
     *
     * @return
     */
    @GetMapping("seedProduceAddressList")
    public ResponseResult<List<SeedProduceAddressListRsp>> seedProduceAddressList() {
        return ResponseResult.getSuccess(dictService.seedProduceAddressList());
    }

    /**
     * 获取物种下材料
     *
     * @return
     */
    @GetMapping("acceptorMaterialList")
    public ResponseResult<List<AcceptorMaterialListRspDTO>> acceptorMaterialList(@RequestParam @Validated @NotBlank(message = "参数缺失：speciesCode") String speciesCode) {
        return ResponseResult.getSuccess(dictService.acceptorMaterialList(speciesCode));
    }

    /**
     * 获取品种
     *
     * @return
     */
    @GetMapping("breedList")
    public ResponseResult<List<BreedListRspDTO>> breedList(@RequestParam String speciesCode) {
        return ResponseResult.getSuccess(dictService.breedList(speciesCode));
    }

    /**
     * 获取品种物种
     *
     * @return
     */
    @GetMapping("speciesBreedList")
    public ResponseResult<List<SpeciesBreedListRspDTO>> speciesBreedList() {
        return ResponseResult.getSuccess(dictService.speciesBreedList());
    }

}
