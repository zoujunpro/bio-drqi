package com.bio.drqi.tc.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.tc.service.dto.TcPollinationDataExcelDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import com.bio.drqi.tc.service.dto.TcTestExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;


@Service("tc_pollination_task_apply")
@Slf4j
public class TcPollinationTaskService extends AbstractTcBaseTaskService {


    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;

    @Resource
    private OssService ossService;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        ValidatorUtil.validator(tcPollinationTaskDTO);
        BeanUtils.trimFiledSpace(tcPollinationTaskDTO);
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            if (!tcPollinationTaskDTO.getPollinationExcelUrl().endsWith("xlsx")) {
                throw new BusinessException("文件格式错误");
            }
            String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcPollinationTaskDTO.getPollinationExcelUrl();
            try {
                ossService.downloadPath(tempFilePath, tcPollinationTaskDTO.getPollinationExcelUrl());
            } catch (Exception e) {
                log.error("【任务工单】文件从oss下载失败", e);
                throw new BusinessException("文件处理异常");
            }
            List<TcPollinationDataExcelDTO> tcPollinationDataExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcPollinationDataExcelDTO.class);

        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
