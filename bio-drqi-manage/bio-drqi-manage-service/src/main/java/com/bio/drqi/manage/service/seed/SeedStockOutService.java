package com.bio.drqi.manage.service.seed;

import com.bio.drqi.manage.seed.SeedStockOutReqDTO;
import com.bio.drqi.manage.seed.SeedStockOutRspDTO;
import com.github.pagehelper.PageInfo;

public interface SeedStockOutService {

    PageInfo<SeedStockOutRspDTO> listPage(SeedStockOutReqDTO seedStockOutReqDTO);
}
