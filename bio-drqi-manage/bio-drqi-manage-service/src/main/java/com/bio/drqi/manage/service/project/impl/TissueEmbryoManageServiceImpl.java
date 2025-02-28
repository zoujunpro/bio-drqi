package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.domain.CerSampleTestTb;
import com.bio.drqi.manage.service.project.TissueEmbryoManageService;
import com.bio.drqi.mapper.CerSampleTestTbMapper;
import com.bio.drqi.manage.tissueEmbryo.TissueEmbryoDataExcelDTO;
import com.bio.drqi.manage.tissueEmbryo.TissueEmbryoDataRspDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class TissueEmbryoManageServiceImpl implements TissueEmbryoManageService {

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Override
    public List<TissueEmbryoDataRspDTO> parseExcel(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        String templateDir = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + suffix;
        try {
            FileUtil.writeBytes(file.getBytes(), templateDir);
        } catch (IOException e) {
            throw new BusinessException("excel文件上传异常");
        }
        List<TissueEmbryoDataExcelDTO> tissueEmbryoDataExcelDTOList = ExcelUtil.readExcel(templateDir, TissueEmbryoDataExcelDTO.class);


        for (TissueEmbryoDataExcelDTO tissueEmbryoDataExcelDTO : tissueEmbryoDataExcelDTOList) {
            List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(tissueEmbryoDataExcelDTO.getSampleCode());
            if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                throw new BusinessException("取样编号不存在:" + tissueEmbryoDataExcelDTO.getSampleCode());
            }
        }
        return BeanUtils.copyListProperties(tissueEmbryoDataExcelDTOList, TissueEmbryoDataRspDTO.class);
    }
}
