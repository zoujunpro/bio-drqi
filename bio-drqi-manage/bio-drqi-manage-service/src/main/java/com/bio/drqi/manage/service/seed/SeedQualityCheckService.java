package com.bio.drqi.manage.service.seed;

import com.bio.drqi.common.dto.PageDTO;
import com.bio.drqi.manage.seed.SeedQualityCheckAddReqDTO;
import com.bio.drqi.manage.seed.SeedQualityCheckEditReqDTO;
import com.bio.drqi.manage.seed.SeedQualityCheckRspDTO;
import com.github.pagehelper.PageInfo;

public interface SeedQualityCheckService {
    PageInfo<SeedQualityCheckRspDTO> listPage(PageDTO pageDTO);

    /**
     * 添加
     *
     * @param seedQualityCheckAddReqDTO
     * @return
     */
    void add(SeedQualityCheckAddReqDTO seedQualityCheckAddReqDTO);

    /**
     * 删除
     *
     * @param seedQualityCheckEditReqDTO
     * @return
     */
    void edit(SeedQualityCheckEditReqDTO seedQualityCheckEditReqDTO);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    void delete(Integer id);
}
