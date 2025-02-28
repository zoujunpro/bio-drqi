package com.bio.drqi.manage.controller.seed;

import com.bio.drqi.manage.service.seed.SeedQualityCheckService;
import com.bio.drqi.manage.seed.SeedQualityCheckReqDTO;
import com.bio.common.core.dto.ResponseResult;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 每日质检考种
 */
@RestController
@RequestMapping("/qualityCheck")
public class SeedQualityCheckController {

    @Resource
    private SeedQualityCheckService seedQualityCheckService;


    /**
     * 模板下载
     * @param httpServletResponse
     */
    @GetMapping("/downTemplate")
    public void downTemplate(HttpServletResponse httpServletResponse) {
        seedQualityCheckService.downTemplate(httpServletResponse);
    }

    /**
     * 字段信息
     * @return
     */
    @GetMapping("/fieldList")
    public ResponseResult<List<Map<String, String>>> fieldList() {
        return ResponseResult.getSuccess(seedQualityCheckService.fieldList());
    }

    /**
     * 字段信息
     * @return
     */
    @GetMapping("/fieldListNotTimeAndSeedNum")
    public ResponseResult<List<Map<String, String>>> fieldListNotTimeAndSeedNum() {
        return ResponseResult.getSuccess(seedQualityCheckService.fieldListNotTimeAndSeedNum());
    }

    /**
     * 上传数据
     * @param file
     * @return
     */

    @PostMapping("/updateLoadData")
    public ResponseResult<String> updateLoadData(@RequestPart MultipartFile file) {
        seedQualityCheckService.updateLoadData(file);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 分页查询
     * @param seedQualityCheckReqDTO
     * @return
     */
    @PostMapping("/listPage")
    public ResponseResult<PageInfo<Map<String,String>>> listPage(@RequestBody SeedQualityCheckReqDTO seedQualityCheckReqDTO) {
        return ResponseResult.getSuccess(seedQualityCheckService.listPage(seedQualityCheckReqDTO));
    }
}
