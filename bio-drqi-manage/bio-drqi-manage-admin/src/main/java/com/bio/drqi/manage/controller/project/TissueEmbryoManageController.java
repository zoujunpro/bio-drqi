package com.bio.drqi.manage.controller.project;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.TissueEmbryoManageService;
import com.bio.drqi.manage.tissueEmbryo.TissueEmbryoDataRspDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 组培管理
 */
@RestController
@RequestMapping("/tissueEmbryo")
public class TissueEmbryoManageController {

    @Resource
    private OssService ossService;

    @Resource
    private TissueEmbryoManageService tissueEmbryoManageService;

    /**
     * 组培标签打印数据模板下载
     */
    @GetMapping("downTestTemplate")
    @WebLog(desc = "组培标签打印数据模板下载")
    public void downTestTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "组培标签打印数据模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("组培标签打印数据模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 解析组培标签打印数据
     * @param file
     * @return
     */
    @PostMapping("parseExcel")
    @WebLog(desc = "解析组培标签打印数据")
    public ResponseResult<List<TissueEmbryoDataRspDTO>> parseExcel(MultipartFile file){
        return ResponseResult.getSuccess(tissueEmbryoManageService.parseExcel(file));
    }
}


