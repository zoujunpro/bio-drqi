package com.bio.drqi.manage.freemarker.handle;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.freemarker.util.HtmlGeneratorUtil;
import com.bio.drqi.manage.freemarker.dto.HtmlGenerateDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HtmlToPDFHandle {


    public static String generateHtml(HtmlGenerateDTO htmlGenerateDTO, String taskType) {
        Map<String, Object> map = new HashMap<>();
        map.put("printTime", htmlGenerateDTO.getPrintTime());
        map.put("printUser", htmlGenerateDTO.getPrintUser());
        map.put("applyName", htmlGenerateDTO.getApplyName());
        map.put("applyDate", htmlGenerateDTO.getApplyDate());
        map.put("taskDesc", htmlGenerateDTO.getTaskDesc());
        map.put("taskType", htmlGenerateDTO.getTaskType());
        map.put("taskNum", htmlGenerateDTO.getTaskNum());
        map.put("deptName", htmlGenerateDTO.getDeptName());
        map.put("approveResult", htmlGenerateDTO.getApproveResult());
        map.put("nodeList", htmlGenerateDTO.getNodeList());
        map.put("contentData", htmlGenerateDTO.getContentData());
        try {
            return HtmlGeneratorUtil.generate(taskType+".ftl", map);
        } catch (Exception e) {
            log.error("html转化失败", e);
            throw new BusinessException("html转化失败");
        }
    }


}
