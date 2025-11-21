package com.bio.drqi.manage.service.plant.impl;

import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.manage.dto.plant.SampleTestDownRepeatSampleTemplateExcelDTO;
import com.bio.drqi.manage.service.plant.PlantSampleTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class PlantSampleTestServiceImpl implements PlantSampleTestService {

    @Override
    public void downRepeatSampleTemplate(HttpServletResponse httpServletResponse) {
        ExcelUtil.writeExcel("重复取样模板","sheet1",null, SampleTestDownRepeatSampleTemplateExcelDTO.class,httpServletResponse);
    }
}
