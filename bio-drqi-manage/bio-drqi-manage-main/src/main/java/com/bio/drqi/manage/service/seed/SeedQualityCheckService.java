package com.bio.drqi.manage.service.seed;

import com.bio.drqi.seed.SeedQualityCheckReqDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface SeedQualityCheckService {
    void downTemplate(HttpServletResponse httpServletResponse);

    List<Map<String, String>> fieldList();

    List<Map<String, String>> fieldListNotTimeAndSeedNum();

    void updateLoadData(MultipartFile multipartFile);

    PageInfo<Map<String, String>> listPage(SeedQualityCheckReqDTO seedQualityCheckReqDTO);
}
