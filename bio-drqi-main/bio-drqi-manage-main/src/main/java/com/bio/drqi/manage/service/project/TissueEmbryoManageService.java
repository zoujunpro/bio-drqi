package com.bio.drqi.manage.service.project;


import com.bio.drqi.tissueEmbryo.TissueEmbryoDataRspDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TissueEmbryoManageService {
    List<TissueEmbryoDataRspDTO> parseExcel(MultipartFile file);
}
