package com.bio.drqi.tc.controller;


import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 授粉收获管理
 */
@RestController
@RequestMapping("/tcPollination")
public class TcPollinationController {

    @Resource
    private TcPollinationService tcPollinationService;

    @Resource
    private OssService ossService;
    /**
     * 授粉管理-分页查询
     *
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "授粉管理-分页查询")
    public ResponseResult<PageInfo<TcPollinationListPageRspDTO>> listPage(@RequestBody @Validated TcPollinationListPageReqDTO tcPollinationListPageReqDTO) {
        return ResponseResult.getSuccess(tcPollinationService.listPage(tcPollinationListPageReqDTO));
    }



    /**
     * 授粉管理-授粉列表分页查询
     *
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "授粉管理-授粉列表分页查询")
    public ResponseResult<PageInfo<TcPollinationListPageDetailRspDTO>> listPageDetail(@RequestBody @Validated TcPollinationListPageDetailReqDTO tcPollinationListPageDetailReqDTO) {
        return ResponseResult.getSuccess(tcPollinationService.listPageDetail(tcPollinationListPageDetailReqDTO));
    }


    /**
     * 授粉管理-生成授粉excel
     */
    @PostMapping("/createPollinationExcel")
    @WebLog(desc = "授粉管理-生成授粉excel")
    public void createPollinationExcel(@RequestBody @Validated TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
            tcPollinationService.createPollinationExcel(tcPollinationCreatePollinationExcelReqDTO,httpServletResponse);
    }





    /**
     * 授粉管理-授粉结果表下载
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/downTemplate")
    @WebLog(desc = "授粉管理-授粉结果表下载")
    public void downTemplate(HttpServletResponse httpServletResponse) {
        try {
            ossService.downloadFile(httpServletResponse, "template", "田测授粉结果表单模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("田测授粉数据表单模板下载失败，请联系管理员检测模板配置");
        }
    }


    public static void main(String[] args) {

        List<TcPollinationExcelDTO> tcPollinationOneExcelDTOList=new ArrayList<>();
        TcPollinationExcelDTO tcPollinationExcelDTO=new TcPollinationExcelDTO();
        tcPollinationExcelDTO.setMotherRegionNum("1");
        tcPollinationExcelDTO.setMotherSeedNum("1");
        tcPollinationExcelDTO.setMotherSampleCode("1");
        tcPollinationExcelDTO.setMotherBreedName("1");
        tcPollinationExcelDTO.setMotherVectorTaskCode("1");
        tcPollinationExcelDTO.setMotherGenerationName("1");
        tcPollinationExcelDTO.setMotherTcGene("1");
        tcPollinationExcelDTO.setFatherBreedName("1");
        tcPollinationExcelDTO.setFatherSeedNum("1");
        tcPollinationExcelDTO.setFatherSampleCode("1");
        tcPollinationExcelDTO.setFatherBreedName("1");
        tcPollinationExcelDTO.setFatherVectorTaskCode("1");
        tcPollinationExcelDTO.setFatherGenerationName("1");
        tcPollinationExcelDTO.setFatherTcGene("1");
        tcPollinationExcelDTO.setPollinationDate("1");
        tcPollinationExcelDTO.setHarvestTypeName("1");
        tcPollinationExcelDTO.setRemark("1");
        tcPollinationOneExcelDTOList.add(tcPollinationExcelDTO);
        //ExcelUtil.fillExcel("D:/2025test.xlsx","C:\\Users\\zou'jun\\Desktop\\田测\\田测授粉数据表单模板V2.0.xlsx", tcPollinationOneExcelDTOList, TcPollinationExcelDTO.class);
     List<TcPollinationExcelDTO> result=   ExcelUtil.readExcel("D:/2025test.xlsx",TcPollinationExcelDTO.class);
     for (TcPollinationExcelDTO tcPollinationExcelDTO1:result){
         System.out.println(JSONUtil.toJsonStr(tcPollinationExcelDTO1));
     }

    }

}
